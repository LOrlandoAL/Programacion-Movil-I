package com.example.cna.ui.Screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cna.componentes.CameraButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    state: NoteState,
    noteId: Int?,
    onEvent: (NoteEvent) -> Unit,
) {
    val viewModel: NoteViewModel = hiltViewModel()
    LaunchedEffect(noteId) {
        noteId?.let { viewModel.loadNoteById(it) }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("C.N.A") },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(NoteEvent.NavigateBack) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF3F51B5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                // Campo de título
                BasicTextField(
                    value = state.title,
                    onValueChange = { onEvent(NoteEvent.TitleChange(it)) },
                    textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (state.title.isEmpty()) {
                            Text(text = "Título nueva nota", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }

            item {
                CameraButton(onImagesCaptured = { uri ->
                    uri?.let {
                        if (!state.imageUris.contains(it.toString())) {
                            onEvent(NoteEvent.AddImage(it.toString()))
                        }
                    }
                })
            }

            // Mostrar imágenes capturadas usando AsyncImage
            items(state.imageUris.size) { index ->
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = state.imageUris[index],
                    contentDescription = "Imagen capturada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contenido
                BasicTextField(
                    value = state.content,
                    onValueChange = { onEvent(NoteEvent.ContentChange(it)) },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (state.content.isEmpty()) {
                            Text(text = "Escribe tu nota aquí...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Botón Guardar
                Button(
                    onClick = { onEvent(NoteEvent.SaveNoteAndNavigateBack) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Guardar Nota")
                }
            }
        }
    }
}

