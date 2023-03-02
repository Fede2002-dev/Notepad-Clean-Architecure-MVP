package com.devbin.domain.usecase

import com.devbin.domain.data.Note
import com.devbin.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class GetNotesUseCase(
    private val noteRepository: NoteRepository
): BaseUseCase<GetNotesUseCase.Params, List<Note>>() {
    class Params
    override suspend fun execute(params: GetNotesUseCase.Params): List<Note> = noteRepository.getNotes()
}
