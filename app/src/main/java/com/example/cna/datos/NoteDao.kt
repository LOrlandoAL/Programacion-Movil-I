package com.example.cna.datos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteEntity?


    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<NoteEntity>>
}
