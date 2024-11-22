package com.example.cna.datos.di

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cna.datos.NoteDao
import com.example.cna.datos.NoteEntity
import com.example.cna.domain.Converters

@Database(entities = [NoteEntity::class], version = 3, exportSchema = true)
@TypeConverters(Converters::class)
abstract  class NoteDatabase : RoomDatabase() {
    abstract val dao: NoteDao
    companion object {
        const val name = "note_db"
    }
}


