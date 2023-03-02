package com.devbin.domain.repository

import com.devbin.domain.data.Note

class NoteRepository(private val noteDataSource: NoteDataSource) {
    suspend fun getNotes(): List<Note> = noteDataSource.getNotes()
    suspend fun addNote(note: Note) = noteDataSource.addNote(note)
    suspend fun editNote(note: Note) = noteDataSource.editNote(note)
    suspend fun getNote(id: Int) = noteDataSource.getNote(id)
}
