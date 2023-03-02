package com.devbin.notepad_clean_architecure_mvp.storage

import android.content.Context
import com.devbin.domain.data.Note
import com.devbin.domain.repository.NoteDataSource
import com.devbin.notepad_clean_architecure_mvp.storage.data.toNote
import com.devbin.notepad_clean_architecure_mvp.storage.data.toNoteEntity

class RoomNoteDataSource(context: Context): NoteDataSource {

    private val noteDao = NotesAppDataBase.getInstance(context).noteDao()

    override suspend fun getNotes(): List<Note> = noteDao.getAllNotes().map { it.toNote() }

    override suspend fun addNote(note: Note) = noteDao.insertNote(note.toNoteEntity())

    override suspend fun editNote(note: Note) = noteDao.updateNote(note.toNoteEntity())

    override suspend fun getNote(id: Int): Note = noteDao.getNote(id).toNote()
}