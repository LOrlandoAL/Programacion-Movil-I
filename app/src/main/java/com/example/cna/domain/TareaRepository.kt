package com.example.cna.domain

import kotlinx.coroutines.flow.Flow

interface TareaRepository {

    fun getAllTasks(): Flow<List<Tarea>>

    suspend fun getTareaById(id: Int): Tarea?

    suspend fun insertTask(tarea: Tarea)

    suspend fun deletetask(tarea: Tarea)

    suspend fun updatetask(tarea: Tarea)
}