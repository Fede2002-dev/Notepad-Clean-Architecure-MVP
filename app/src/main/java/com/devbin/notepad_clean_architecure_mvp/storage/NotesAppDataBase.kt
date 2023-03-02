package com.devbin.notepad_clean_architecure_mvp.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devbin.notepad_clean_architecure_mvp.storage.data.NoteDao
import com.devbin.notepad_clean_architecure_mvp.storage.data.NoteEntity

@Database(entities = [NoteEntity::class], version = 1)
abstract class NotesAppDataBase: RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private const val DATABASE_NAME = "notes-app-database"
        private var instance: NotesAppDataBase? = null

        private fun create(context: Context): NotesAppDataBase {
            val db = Room.databaseBuilder(
                context.applicationContext,
                NotesAppDataBase::class.java, DATABASE_NAME
            ).build()
            instance = db
            return db
        }

        fun getInstance(context: Context) = instance ?: create(context)
    }
}
