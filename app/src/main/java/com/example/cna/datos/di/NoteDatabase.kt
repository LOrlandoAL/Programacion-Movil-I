package com.example.cna.datos.di

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cna.datos.NoteDao
import com.example.cna.datos.NoteEntity
import com.example.cna.datos.TareaDao
import com.example.cna.datos.TareaEntity
import com.example.cna.domain.Converters

@Database(entities = [NoteEntity::class, TareaEntity::class], version = 6, exportSchema = true)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val taskDao: TareaDao
    companion object {
        const val name = "note_db"
    }
}



