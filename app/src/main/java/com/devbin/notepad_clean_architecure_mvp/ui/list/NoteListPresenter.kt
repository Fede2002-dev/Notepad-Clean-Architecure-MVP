package com.devbin.notepad_clean_architecure_mvp.ui.list

import com.devbin.domain.usecase.GetNotesUseCase

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
