package com.example.cna.datos.mapper

import com.example.cna.datos.NoteEntity
import com.example.cna.domain.Note

fun NoteEntity.asExternalModel(): Note = Note(
    id, title, content, imageUris
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id, title, content, imageUris
)