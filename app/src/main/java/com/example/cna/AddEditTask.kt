package com.example.cna
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.cna.theme.CNATheme
import com.example.cna.ui.Screen.TaskViewModel
import com.example.cna.ui.Screen.taskScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTask : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tareaId = intent.getIntExtra("noteId", -1).takeIf { it != -1 } // Obtén el id de la nota si existe

        setContent {
            CNATheme {
                val state by viewModel.state.collectAsState()
                taskScreen(
                    state = state,
                    tareaid = tareaId, // Pasa el id al componente NoteScreen
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}