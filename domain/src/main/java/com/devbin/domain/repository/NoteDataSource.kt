package com.devbin.domain.repository

import com.devbin.domain.data.Note

interface NoteDataSource {
    suspend fun getNotes(): List<Note>
    suspend fun addNote(note: Note)
    suspend fun editNote(note: Note)
    suspend fun getNote(id: Int): Note
}