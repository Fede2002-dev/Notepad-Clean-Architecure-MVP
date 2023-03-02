package com.devbin.notepad_clean_architecure_mvp.ui.list

import com.devbin.domain.data.Note

interface NoteListViewContract {
    fun updateNotesList(notes: List<Note>)
    fun onFailure(cause: String)
}
