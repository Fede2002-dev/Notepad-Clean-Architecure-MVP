package com.devbin.notepad_clean_architecure_mvp.storage.data

import com.devbin.domain.data.Note

fun NoteEntity.toNote() = Note(id, title, content, date)

fun Note.toNoteEntity() = NoteEntity(id, title, content, date)