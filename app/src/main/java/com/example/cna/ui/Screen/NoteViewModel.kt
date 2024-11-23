package com.example.cna.ui.Screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cna.domain.Note
import com.example.cna.domain.NoteRepository
import com.example.cna.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val notes: Flow<List<Note>> = repository.getAllNotes()

    private val _state = MutableStateFlow(NoteState())
    var state: StateFlow<NoteState> = _state.asStateFlow()

    private val _event = Channel<UiEvent>()
    val event: Flow<UiEvent> = _event.receiveAsFlow()

    init {
        savedStateHandle.get<String>("id")?.let { id ->
            viewModelScope.launch {
                repository.getNoteById(id.toInt())?.let { note ->
                    _state.update { screenState ->
                        screenState.copy(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            imageUris = note.imageUris,
                            AudioUris = note.AudioUris,
                            videosUris = note.videosUris
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.ContentChange -> {
                _state.update { it.copy(content = event.value) }
            }
            is NoteEvent.TitleChange -> {
                _state.update { it.copy(title = event.value) }
            }
            is NoteEvent.AddImage -> {
                _state.update { current ->
                    val uniqueUris = (current.imageUris + event.uri).distinct()
                    current.copy(imageUris = uniqueUris)
                }

            }
            is NoteEvent.AddAudio -> {
                _state.update { current ->
                    val uniqueUris = (current.AudioUris + event.uri).distinct()
                    current.copy(AudioUris = uniqueUris)
                }
            }
            is NoteEvent.AddVideo -> {
                _state.update { current ->
                    val uniqueUris = (current.videosUris + event.uri).distinct()
                    current.copy(videosUris = uniqueUris)
                }
            }

            NoteEvent.NavigateBack -> {
                sendEvent(UiEvent.NavigateBack)
            }
            NoteEvent.SaveNoteAndNavigateBack -> {
                saveNote {
                    sendEvent(UiEvent.NavigateBack)
                }
            }

            NoteEvent.Save -> {
                saveNote()
            }
            NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    state.value.id?.let { id ->
                        val note = Note(
                            id = id,
                            title = state.value.title,
                            content = state.value.content,
                            imageUris = state.value.imageUris,
                            AudioUris = state.value.AudioUris,
                            videosUris = state.value.videosUris
                        )
                        repository.deleteNote(note)
                    }
                    sendEvent(UiEvent.NavigateBack)
                }
            }
        }
    }

    private fun saveNote(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val currentState = state.value
            val note = Note(
                id = currentState.id,
                title = currentState.title,
                content = currentState.content,
                imageUris = currentState.imageUris.distinct(),
                AudioUris = currentState.AudioUris.distinct(),
                videosUris= currentState.videosUris.distinct()
            )
            if (currentState.id == null) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
            }
            onComplete?.invoke()
        }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            repository.getNoteById(noteId)?.let { note ->
                _state.value = _state.value.copy(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    imageUris = note.imageUris.distinct(),
                    AudioUris = note.AudioUris.distinct(),
                    videosUris = note.videosUris.distinct()
                )
            }
        }
    }

}

