package com.devbin.notepad_clean_architecure_mvp.ui.note

import com.devbin.domain.data.Note
import com.devbin.domain.usecase.AddNoteUseCase
import com.devbin.domain.usecase.EditNoteUseCase
import com.devbin.domain.usecase.GetNoteWithIdUseCase

class NotePresenter(
    private val mView: NoteViewContract,
    private val addNoteUseCase: AddNoteUseCase,
    private val getNoteWithIdUseCase: GetNoteWithIdUseCase,
    private val editNoteUseCase: EditNoteUseCase
) {
    private var editingNoteId: Int? = null

    fun createNote(title: String, content: String) {
        val params = AddNoteUseCase.Params(title, content)
        addNoteUseCase.invoke(
            params = params,
            onError = { it.message?.let { message -> mView.postMessage(message) } },
            onSuccess = { mView.onNoteCreated() }
        )
    }

    fun getNoteWithId(id: Int) {
        getNoteWithIdUseCase.invoke(
            id,
            onError = { mView.destroyView() },
            onSuccess = {
                editingNoteId = it.id
                mView.setNote(it)
            }
        )
    }

    fun editNote(title: String, content: String) {
        val id: Int = editingNoteId ?: run {
            mView.postMessage("Empty id")
            mView.destroyView()
            return
        }
        editNoteUseCase.invoke(
            EditNoteUseCase.Params(id, title, content),
            onError = { it.message?.let { message -> mView.postMessage(message) } },
            onSuccess = { mView.onNoteEdited() }
        )
    }

    fun onDestroy() {
        addNoteUseCase.cancel()
        getNoteWithIdUseCase.cancel()
        editNoteUseCase.cancel()
    }
}
