package com.devbin.domain.usecase

import com.devbin.domain.data.Note
import com.devbin.domain.getDate
import com.devbin.domain.repository.NoteRepository

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
