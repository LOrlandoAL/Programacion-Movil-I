package com.example.cna

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cna.ui.theme.CNATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CNATheme {
                val navController = rememberNavController()
                var showSaveButton by remember { mutableStateOf(false) } // Controlar cuándo mostrar el botón de guardar

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar(showSaveButton, onSaveClick = {
                        // Al hacer clic en guardar, mostramos un mensaje y navegamos a la pantalla principal
                        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Volver a la pantalla anterior
                    }) },
                    floatingActionButton = {
                        // Mostrar el botón solo si no estamos en la pantalla de edición
                        if (!showSaveButton) {
                            AddNoteButton(navController)
                        }
                    }
                ) { innerPadding ->
                    NavigationHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToEditNote = { showSaveButton = true }, // Mostrar botón de guardar
                        onNavigateBack = { showSaveButton = false } // Ocultar el botón de guardar al volver
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToEditNote: () -> Unit,
    onNavigateBack: () -> Unit
) {
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(modifier = modifier)
            onNavigateBack()
        }
        composable("addEditNote") {
            NoteScreen()
            onNavigateToEditNote()
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Frase del dia
        QuoteOfTheDay(
            text = "Yo solo sé que tú y yo no sabemos nada.",
            author = "Rogelio Fabricio (2022)"
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color(0xFF3F51B5))
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de notas
        NoteList(
            notes = listOf(
                "Nota 1" to "Contenido breve de la nota 1",
                "Nota 2" to "Contenido breve de la nota 2",
                "Nota 3" to "Contenido breve de la nota 3",
                "Nota 4" to "Contenido breve de la nota 4"
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(showSaveButton: Boolean, onSaveClick: () -> Unit) {
    TopAppBar(
        title = { Text("C.N.A") },
        actions = {
            if (showSaveButton) {
                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Guardar"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0xFF3F51B5),
            titleContentColor = Color.White
        )
    )
}

@Composable
fun AddNoteButton(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            navController.navigate("addEditNote")
        },
        containerColor = Color.Transparent,
        modifier = Modifier.size(56.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3F51B5),
                            Color(0xFF7CB5FF)
                        )
                    ),
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar Nota",
                tint = Color.White
            )
        }
    }
}
@Composable
fun QuoteOfTheDay(text: String, author: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "- $author",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun NoteList(notes: List<Pair<String, String>>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(notes.size) { index ->
            NoteCard(title = notes[index].first, content = notes[index].second)
        }
    }
}

@Composable
fun NoteCard(title: String, content: String) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0E6F8)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                maxLines = 3,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CNATheme {
        MainScreen()
    }
}
