package com.example.cna.datos.mapper

import com.example.cna.datos.NoteEntity
import com.example.cna.datos.TareaEntity
import com.example.cna.domain.Note
import com.example.cna.domain.Tarea

fun NoteEntity.asExternalModel(): Note = Note(
    id, title, content, imageUris, AudioUris,videosUris
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id, title, content, imageUris, AudioUris,videosUris
)
fun TareaEntity.asExternalModel(): Tarea = Tarea(
    id, title, content, imageUris, AudioUris,videosUris,dateTimeMillis, isCompleted
)

fun Tarea.toEntity(): TareaEntity = TareaEntity(
    id, title, content, imageUris, AudioUris,videosUris, dateTimeMillis, isCompleted
)