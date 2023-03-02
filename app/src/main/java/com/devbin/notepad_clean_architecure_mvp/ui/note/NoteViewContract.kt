package com.devbin.notepad_clean_architecure_mvp.ui.note

import com.devbin.domain.data.Note

interface NoteViewContract {
    fun onNoteCreated()
    fun onNoteEdited()
    fun postMessage(cause: String)
    fun destroyView()
    fun setNote(note: Note)
}
