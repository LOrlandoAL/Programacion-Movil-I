package com.example.cna.ui.Screen

sealed interface NoteEvent {
    data class TitleChange(val value: String): NoteEvent
    data class ContentChange(val value: String): NoteEvent
    data class AddImage(val uri: String) : NoteEvent
    data class AddAudio(val uri: String): NoteEvent
    data class AddVideo(val uri: String): NoteEvent
    data class RemoveImage(val uri: String) : NoteEvent
    data class RemoveAudio(val uri: String): NoteEvent
    data class RemoveVideo(val uri: String): NoteEvent
    object Save : NoteEvent
    object NavigateBack : NoteEvent
    object DeleteNote : NoteEvent
    object SaveNoteAndNavigateBack : NoteEvent
}