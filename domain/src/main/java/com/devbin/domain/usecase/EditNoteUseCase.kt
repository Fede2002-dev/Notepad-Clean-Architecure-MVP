package com.devbin.domain.usecase

import com.devbin.domain.data.Note
import com.devbin.domain.getDate
import com.devbin.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNoteUseCase(
    private val noteRepository: NoteRepository
): BaseUseCase<EditNoteUseCase.Params, Boolean>() {

    data class Params(
        val id: Int,
        val title: String,
        val content: String
    )

    class CouldNotEditNoteException(cause: String?): Exception(cause)

    override suspend fun execute(params: Params): Boolean {
        // Throws exception if title is empty, following guard pattern
        if(params.title.isBlank()) {
            throw CouldNotEditNoteException("Empty title")
        }
        // Throws exception if content is empty, following guard pattern
        if(params.content.isBlank()) {
            throw CouldNotEditNoteException("Empty content")
        }
        val note = Note(id = params.id, title = params.title, content = params.content, date = getDate())
        noteRepository.editNote(note)
        return true
    }
}
