package com.devbin.notepad_clean_architecure_mvp.ui.note

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devbin.domain.data.Note
import com.devbin.domain.repository.NoteRepository
import com.devbin.domain.usecase.AddNoteUseCase
import com.devbin.domain.usecase.EditNoteUseCase
import com.devbin.domain.usecase.GetNoteWithIdUseCase
import com.devbin.notepad_clean_architecure_mvp.databinding.ActivityNoteBinding
import com.devbin.notepad_clean_architecure_mvp.storage.RoomNoteDataSource
import com.devbin.notepad_clean_architecure_mvp.ui.list.NoteListActivity

class NoteActivity : AppCompatActivity(), NoteViewContract {

    private val binding by lazy {
        ActivityNoteBinding.inflate(layoutInflater)
    }
    private val presenter by lazy {
        val repository = NoteRepository(RoomNoteDataSource(this))
        NotePresenter(
        this,
            AddNoteUseCase(repository),
            GetNoteWithIdUseCase(repository),
            EditNoteUseCase(repository)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.backButton.setOnClickListener { finish() }

        val extras = intent.extras
        when(extras?.getInt(MODE_KEY)) {
            CREATE_MODE -> setupActivityForCreate()
            EDIT_MODE -> {
                val id = extras.getInt(NOTE_ID_KEY, EMPTY_NOTE_ID)
                if (id == EMPTY_NOTE_ID) finish()
                else setupActivityForEdit(id)
            }
            else -> setupActivityForCreate()
        }
    }

    private fun setupActivityForCreate() {
        with(binding) {
            saveButton.setOnClickListener {
                presenter.createNote(noteTitle.text.toString(), noteContent.text.toString())
            }
        }
    }

    private fun setupActivityForEdit(id: Int) {
        presenter.getNoteWithId(id)
        with(binding) {
            saveButton.setOnClickListener {
                presenter.editNote(noteTitle.text.toString(), noteContent.text.toString())
            }
        }
    }


    override fun onNoteCreated() {
        setResult(NoteListActivity.RESULT_NOTE_CREATED)
        finish()
    }

    override fun onNoteEdited() {
        onNoteCreated()
    }

    override fun postMessage(cause: String) {
        Toast.makeText(applicationContext, cause, Toast.LENGTH_SHORT).show()
    }

    override fun destroyView() {
        finish()
    }

    override fun setNote(note: Note) {
        with(binding) {
            noteTitle.setText(note.title)
            noteContent.setText(note.content)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    companion object {
        const val EDIT_MODE = 15
        const val CREATE_MODE = 14
        const val MODE_KEY = "mode"
        const val NOTE_ID_KEY = "note_id"
        const val EMPTY_NOTE_ID = -1
    }
}
