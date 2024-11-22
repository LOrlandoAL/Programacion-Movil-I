package com.example.cna.ui.Screen

data class NoteState(
    val id: Int? = null,
    val title: String = "",
    val content: String = "",
    val imageUris: List<String> = emptyList()
)