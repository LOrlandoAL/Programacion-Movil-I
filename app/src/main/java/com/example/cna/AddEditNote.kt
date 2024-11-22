package com.example.cna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cna.theme.CNATheme
import com.example.cna.ui.Screen.NoteEvent
import com.example.cna.ui.Screen.NoteScreen
import com.example.cna.ui.Screen.NoteState
import com.example.cna.ui.Screen.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditNote : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getIntExtra("noteId", -1).takeIf { it != -1 } // Obtén el id de la nota si existe

        setContent {
            CNATheme {
                val state by viewModel.state.collectAsState()
                NoteScreen(
                    state = state,
                    noteId = noteId, // Pasa el id al componente NoteScreen
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}






