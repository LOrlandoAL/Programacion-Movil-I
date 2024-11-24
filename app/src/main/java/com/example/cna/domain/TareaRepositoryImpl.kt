package com.example.cna.domain

import com.example.cna.datos.TareaDao
import com.example.cna.datos.mapper.asExternalModel
import com.example.cna.datos.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TareaRepositoryImpl(
    private val dao: TareaDao
) : TareaRepository {

    override fun getAllTasks(): Flow<List<Tarea>> {
        return dao.getAllTasks()
            .map { tareas ->
                tareas.map {
                    it.asExternalModel()
                }
            }
    }

    override suspend fun getTareaById(id: Int): Tarea? {
        return dao.getTaskById(id)?.asExternalModel()
    }

    override suspend fun insertTask(tarea: Tarea) {
        dao.insertTask(tarea.toEntity())
    }

    override suspend fun deletetask(tarea: Tarea) {
        dao.deleteTask(tarea.toEntity())
    }

    override suspend fun updatetask(tarea: Tarea) {
        dao.updateTask(tarea.toEntity())
    }
}