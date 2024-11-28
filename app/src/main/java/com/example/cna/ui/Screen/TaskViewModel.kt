package com.example.cna.ui.Screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cna.domain.Tarea
import com.example.cna.domain.TareaRepository
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
class TaskViewModel @Inject constructor(
    private val repository: TareaRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val tasks: Flow<List<Tarea>> = repository.getAllTasks()

    private val _state = MutableStateFlow(TareaState())
    var state: StateFlow<TareaState> = _state.asStateFlow()

    private val _event = Channel<UiEvent>()
    val event: Flow<UiEvent> = _event.receiveAsFlow()

    init {
        savedStateHandle.get<String>("id")?.let { id ->
            viewModelScope.launch {
                repository.getTareaById(id.toInt())?.let { tarea ->
                    _state.update { screenState ->
                        screenState.copy(
                            id = tarea.id,
                            title = tarea.title,
                            content = tarea.content,
                            imageUris = tarea.imageUris,
                            AudioUris = tarea.AudioUris,
                            videosUris = tarea.videosUris,
                            dateTimeMillis = tarea.dateTimeMillis,
                            isCompleted = tarea.isCompleted
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: TareaEvent) {
        when (event) {
            is TareaEvent.ContentChange -> {
                _state.update { it.copy(content = event.value) }
            }
            is TareaEvent.TitleChange -> {
                _state.update { it.copy(title = event.value) }
            }
            is TareaEvent.AddImage -> {
                _state.update { current ->
                    val uniqueUris = (current.imageUris + event.uri).distinct()
                    current.copy(imageUris = uniqueUris)
                }

            }
            is TareaEvent.AddAudio -> {
                _state.update { current ->
                    val uniqueUris = (current.AudioUris + event.uri).distinct()
                    current.copy(AudioUris = uniqueUris)
                }
            }
            is TareaEvent.AddVideo -> {
                _state.update { current ->
                    val uniqueUris = (current.videosUris + event.uri).distinct()
                    current.copy(videosUris = uniqueUris)
                }
            }
            is TareaEvent.DateChange -> {
                _state.value = _state.value.copy(dateTimeMillis = event.dateTimeMillis)
            }
            is TareaEvent.CompleteStatusChange -> {
                _state.value = _state.value.copy(isCompleted = event.isComplete)
            }
            is TareaEvent.RemoveImage -> {
                _state.value = _state.value.copy(
                    imageUris = _state.value.imageUris.filter { it != event.uri }
                )
            }
            is TareaEvent.RemoveAudio -> {
                _state.value = _state.value.copy(
                    AudioUris = _state.value.AudioUris.filter { it != event.uri }
                )
            }
            is TareaEvent.RemoveVideo -> {
                _state.value = _state.value.copy(
                    videosUris = _state.value.videosUris.filter { it != event.uri }
                )
            }

            TareaEvent.NavigateBack -> {
                sendEvent(UiEvent.NavigateBack)
            }
            TareaEvent.SaveTaskAndNavigateBack -> {
                SaveTask {
                    sendEvent(UiEvent.NavigateBack)
                }
            }

            TareaEvent.Save -> {
                SaveTask()
            }
            TareaEvent.DeleteTask -> {
                viewModelScope.launch {
                    state.value.id?.let { id ->
                        val tarea= Tarea(
                            id = id,
                            title = state.value.title,
                            content = state.value.content,
                            imageUris = state.value.imageUris,
                            AudioUris = state.value.AudioUris,
                            videosUris = state.value.videosUris,
                            dateTimeMillis = state.value.dateTimeMillis ?: System.currentTimeMillis(),
                            isCompleted = state.value.isCompleted
                        )
                        repository.deletetask(tarea)
                    }
                    sendEvent(UiEvent.NavigateBack)
                }
            }
        }
    }

    private fun SaveTask(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val currentState = state.value
            val tarea = Tarea(
                id = currentState.id,
                title = currentState.title,
                content = currentState.content,
                imageUris = currentState.imageUris.distinct(),
                AudioUris = currentState.AudioUris.distinct(),
                videosUris= currentState.videosUris.distinct(),
                dateTimeMillis = currentState.dateTimeMillis ?: System.currentTimeMillis(),
                isCompleted= currentState.isCompleted
            )
            if (currentState.id == null) {
                repository.insertTask(tarea)
            } else {
                repository.updatetask(tarea)
            }
            onComplete?.invoke()
        }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
    fun loadTaskById(tareaId: Int) {
        viewModelScope.launch {
            repository.getTareaById(tareaId)?.let { tarea ->
                _state.value = _state.value.copy(
                    id = tarea.id,
                    title = tarea.title,
                    content = tarea.content,
                    imageUris = tarea.imageUris.distinct(),
                    AudioUris = tarea.AudioUris.distinct(),
                    videosUris = tarea.videosUris.distinct(),
                    dateTimeMillis = tarea.dateTimeMillis,
                    isCompleted = tarea.isCompleted
                )
            }
        }
    }

}

