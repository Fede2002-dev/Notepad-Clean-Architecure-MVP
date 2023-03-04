# Notepad-Clean-Architecure-MVP
## Description
In this repo I will provide an example implementation of Clean architecture combined with MVP. I will use a practical example of a notes app and I will 
releasing comments about why and how. I also will provide another resources that will help you to clarify all those concepts touched in this app.

Well, let's start!

## WTH is Clean Architecture
Clean architecture is a way to develop software per layers, every inner layer should not depend on a outer layer, which makes software highly desacoupled. The inner layers should contain every code related to business logic, meanhwhile outer layers contain implementations and framework things.
<div align="center">
    <img width=500 src="https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg">
</div>

As the image above shows, in clean only the outer layers can access to inner layers

In this repo, I will use this three layers, but you can create many layers as you need: 

- __Domain:__ where business logic should be placed, contains data (usually kotlin data classes), use cases and repository abstractions
- __Storage:__ where are database implementations (Room)
- __UI:__ here we put every related to views (in this case would be Activitys) and presentation classes such as Presenter's

It's recommended start writing code from inner layers to outer, so les continue with;

## The Domain layer
This layer will be placed in its own module, as a kotlin library, and it will have three sub packages (sub-layers): __data__, __repository__ and __usecases__

__Data__ 

For the notes app we only need one data class, that will be Note
```kotlin
data class Note(
    val id: Int = 0,
    val title: String,
    val content: String,
    val date: String
)
```
__Repository__ 

This layer will be conformed by the data source to be implemented by the framework, here we declare all interactions that we want to perform using the data, I will use coroutines here.

```kotlin
interface NoteDataSource {
    suspend fun getNotes(): List<Note>
    suspend fun addNote(note: Note)
    suspend fun editNote(note: Note)
    suspend fun getNote(id: Int): Note
}
```
Also, this layer have the repository implementation:
```kotlin
class NoteRepository(private val noteDataSource: NoteDataSource) {
    suspend fun getNotes(): List<Note> = noteDataSource.getNotes()
    suspend fun addNote(note: Note) = noteDataSource.addNote(note)
    suspend fun editNote(note: Note) = noteDataSource.editNote(note)
    suspend fun getNote(id: Int) = noteDataSource.getNote(id)
}
```

__Data Sources__

Here is where all business logic interactors will be implemented to be used for the framework, I will start defining a base use case, which will help me to keep the things simple in the framework layer, later you will see that execute a usecase will be very trivial:
```kotlin
abstract class BaseUseCase<in ParamsType, out ReturnType> {

    private var job: Job? = null

    open val dispatcher: CoroutineDispatcher = Dispatchers.IO
    open val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    // This fun will be used to provide the actual implementation
    // in the child class
    abstract suspend fun execute(params: ParamsType): ReturnType

    // This will enable you to call the useCase as function
    // example sendPayment(params)
    // it will run the buildUseCase function using the provided dispatcher
    operator fun invoke(params: ParamsType, onError: (Throwable) -> Unit, onSuccess: (ReturnType) -> Unit) {
        job = CoroutineScope(dispatcher).launch {
            try {
                val response = execute(params)
                withContext(mainDispatcher) { onSuccess(response) }
            } catch (e: Exception) {
                withContext(mainDispatcher) { onError(e) }
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }
}
```

In this case, I will define the following use cases: AddNoteUseCase, EditNoteUseCase, GetNoteWithIdUseCase and GetNotesUseCase. Here is an example implementation using the BaseUseCase: 
```kotlin
class AddNoteUseCase(
    private val noteRepository: NoteRepository
): BaseUseCase<AddNoteUseCase.Params, Boolean>() {

    data class Params(
        val title: String,
        val content: String
    )

    class CouldNotAddNoteException(cause: String?): Exception(cause)

    override suspend fun execute(params: Params): Boolean {
        // Throws exception if title is empty, following guard pattern
        if (params.title.isBlank()) throw CouldNotAddNoteException("empty title")
        // Throws exception if content is empty, following guard pattern
        if (params.content.isBlank()) throw CouldNotAddNoteException("empty content")

        val note = Note(title = params.title, content = params.content, date = getDate())
        noteRepository.addNote(note)
        return true
    }
}
```

Now we have finish our job with the domain layer, lets move to framework

## Storage layer
For storage I will be using Room, which provides an abstraction layer over SQLite. \
This layer will have it's own "data layer" because we need to declare the note as an Entity, that is framework dependent.
```kotlin
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "date") val date: String
)
```
Also, we need to convert the NoteEntity object to Note, so I will use kotlin extension functions to achieve this: 
```kotlin
fun NoteEntity.toNote() = Note(id, title, content, date)

fun Note.toNoteEntity() = NoteEntity(id, title, content, date)
```

Then, we should implement the data source declared in domain layer: 
```kotlin
class RoomNoteDataSource(context: Context): NoteDataSource {

    private val noteDao = NotesAppDataBase.getInstance(context).noteDao()

    override suspend fun getNotes(): List<Note> = noteDao.getAllNotes().map { it.toNote() }

    override suspend fun addNote(note: Note) = noteDao.insertNote(note.toNoteEntity())

    override suspend fun editNote(note: Note) = noteDao.updateNote(note.toNoteEntity())

    override suspend fun getNote(id: Int): Note = noteDao.getNote(id).toNote()
}
```

Now we have finished the storage layer, lets move to presentation (or UI):

## Presentation layer
First I will introduce briefly MVP (or Model-View-Presenter), that is an architectural pattern that help us to decouple logic from view, the components of this pattern are:

- __Model:__ in this case will be the domain layer declared before
- __View:__ all activities, fragments, etc.
- __Presenter:__ Will communicate to model and notify the responses to view

First, we declare a contract that view should implement, here I will cover only the retrieve of all notes from storage, but the principle is the same for all the others use cases, you can find the code in repo.
```kotlin
interface NoteListViewContract {
    fun updateNotesList(notes: List<Note>)
    fun onFailure(cause: String)
}
```
Then, presenter should be created:  
```kotlin
class NoteListPresenter(
    private val mView: NoteListViewContract,
    private var getNotesUseCase: GetNotesUseCase
    ) {

    fun getAllNotes() {
        getNotesUseCase.invoke(
            GetNotesUseCase.Params(),
            onError = { it.message?.let { message -> mView.onFailure(message) } },
            onSuccess = { mView.updateNotesList(it) }
        )
    }

    fun onDestroy() {
        getNotesUseCase.cancel()
    }
}
```

As you can see, because we implemented all business logic on the use cases, we only have to invoke the use case providing the params needed, the onError block and onSuccess block. In on destroy we cancel the coroutine job to release resources and avoid memory leaks.

At last, we create the view that implements contract: 
```kotlin

    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        presenter.getAllNotes()
    }

    override fun updateNotesList(notes: List<Note>) {
        adapter.noteList = notes
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(cause: String) {
        Toast.makeText(applicationContext, cause, Toast.LENGTH_SHORT).show()
    }
```
Feel free to navigate project to see all anothers use cases and it implementations!
