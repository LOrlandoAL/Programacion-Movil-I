package com.example.cna.ui.Screen

data class TareaState (
    val id: Int? = null,
    val title: String = "",
    val content: String = "",
    val imageUris: List<String> = emptyList(),
    val AudioUris: List<String> = emptyList(),
    val videosUris: List<String> = emptyList(),
    val dateTimeMillis: Long? =  null,
    val isCompleted: Boolean = false
)