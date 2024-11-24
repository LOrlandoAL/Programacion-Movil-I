package com.example.cna.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task:TareaEntity)

    @Delete
    suspend fun deleteTask(task: TareaEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TareaEntity?

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TareaEntity>>
    @Update
    suspend fun updateTask(tareaEntity: TareaEntity)
}
