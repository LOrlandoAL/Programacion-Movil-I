package com.example.cna.ui.Screen

sealed interface TareaEvent {
    data class TitleChange(val value: String): TareaEvent
    data class ContentChange(val value: String): TareaEvent
    data class AddImage(val uri: String) : TareaEvent
    data class AddAudio(val uri: String): TareaEvent
    data class AddVideo(val uri: String): TareaEvent
    data class DateChange(val dateTimeMillis: Long?) : TareaEvent // Evento para cambiar la fecha
    data class CompleteStatusChange(val isComplete: Boolean) : TareaEvent // Evento para cambiar el estado de completado
    object Save : TareaEvent
    object NavigateBack : TareaEvent
    object DeleteTask : TareaEvent
    object SaveTaskAndNavigateBack : TareaEvent
}