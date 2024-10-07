package com.example.cna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AddEditNote : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen() {
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("C.N.A") },
                actions = {
                    TextButton(onClick = {
                        // Funcionalidad de Guardado (Por Implementar)
                    }) {
                        Text("Guardar", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF3F51B5),
                    titleContentColor = Color.White
                ),
                modifier = Modifier.background(Color(0xFF3F51B5)) // Color del top bar
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.LightGray)
                    .padding(16.dp),
                decorationBox = { innerTextField ->
                    if (noteTitle.isEmpty()) {
                        Text(text = "Título nueva nota", color = Color.Gray)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.LightGray)
                    .padding(16.dp),
                decorationBox = { innerTextField ->
                    if (noteContent.isEmpty()) {
                        Text(text = "Escribe tu nota aquí...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteScreen() {
    NoteScreen()
}
