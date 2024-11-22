package com.example.cna.util

sealed interface UiEvent {
    data class Navigate(val route: String): UiEvent
    object NavigateBack : UiEvent
}