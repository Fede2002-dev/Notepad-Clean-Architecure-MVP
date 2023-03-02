package com.devbin.domain.usecase

import com.devbin.domain.data.Note
import com.devbin.domain.repository.NoteRepository

class GetNoteWithIdUseCase(
    private val noteRepository: NoteRepository
): BaseUseCase<Int, Note>() {
    override suspend fun execute(params: Int): Note = noteRepository.getNote(params)
}