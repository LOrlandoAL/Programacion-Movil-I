package com.example.cna
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cna.theme.CNATheme
import kotlin.random.Random
import com.example.cna.ui.Screen.NoteEvent
import com.example.cna.ui.Screen.NoteScreen
import com.example.cna.ui.Screen.TareaEvent
import com.example.cna.ui.Screen.TaskViewModel
import com.example.cna.ui.Screen.taskScreen
import dagger.hilt.android.AndroidEntryPoint
import com.example.cna.ui.Screen.NoteViewModel as NoteViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CNATheme {
                val navController = rememberNavController()
                val showFloatingButtons = remember { mutableStateOf(true) } // Estado mutable para los botones

                // Listener para ocultar o mostrar botones basado en la ruta
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    showFloatingButtons.value = when (destination.route) {
                        "mainScreen" -> true // Mostrar botones solo en la pantalla principal
                        else -> false // Ocultar en otras pantallas
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppBar(
                            showSaveButton = showFloatingButtons.value.not(), // Mostrar botón de guardar cuando los botones desaparecen
                            onSaveClick = {
                                Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        )
                    },
                    floatingActionButton = {
                        if (showFloatingButtons.value) { // Renderizar botones solo si el estado es true
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp, bottom = 16.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        try {
                                            navController.navigate("AddEditNote/-1")
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "Navigation error: ${e.message}")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NoteAdd,
                                        contentDescription = "Agregar nota"
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                FloatingActionButton(
                                    onClick = {
                                        try {
                                            navController.navigate("AddEditTask/-1")
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "Navigation error: ${e.message}")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = "Agregar tarea"
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavigationHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToEditNote = {},
                        onNavigateBack = {}
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
            MainScreen(
                modifier = modifier,
                onEditNote = { noteId ->
                    navController.navigate("AddEditNote/$noteId")
                },
                onEditTask = { tareaId ->
                    navController.navigate("AddEditTask/$tareaId")
                }
            )
        }
        composable("AddEditNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            val viewModel: NoteViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            NoteScreen(
                state = state,
                noteId = noteId,
                onEvent = { event ->
                    when (event) {
                        is NoteEvent.NavigateBack -> {
                            navController.popBackStack()
                        }
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
        composable("AddEditTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            val taskViewModel: TaskViewModel = hiltViewModel()
            val state by taskViewModel.state.collectAsState()

            taskScreen(
                state = state,
                tareaid = taskId,
                onEvent = { event ->
                    when (event) {
                        is TareaEvent.NavigateBack -> {
                            navController.popBackStack()
                        }
                        else -> taskViewModel.onEvent(event)
                    }
                }
            )
        }
    }
}


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onEditNote: (Int) -> Unit,
    onEditTask: (Int) -> Unit
) {
    val noteViewModel: NoteViewModel = hiltViewModel()
    val taskViewModel: TaskViewModel = hiltViewModel()

    val notes by noteViewModel.notes.collectAsState(initial = emptyList())
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())

    val (indice, frase) = Frases()
    val autor = Autores(indice)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mostrar la frase del día
        QuoteOfTheDay(
            text = frase,
            author = autor,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Agregar un separador horizontal
        HorizontalDivider(thickness = 2.dp, color = Color(0xFF3F51B5))
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de Notas
        Text(
            text = "Notas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        NoteList(
            notes = notes.map { (it.id ?: -1) to (it.title to it.content) },
            onEditNote = onEditNote
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Tareas
        Text(
            text = "Tareas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        TaskList(
            tasks = tasks.map { (it.id ?: -1) to (it.title to it.content) },
            onEditTask = onEditTask
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
fun QuoteOfTheDay(text: String, author: String,) {
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
fun NoteList(notes: List<Pair<Int, Pair<String, String>>>, onEditNote: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(notes.size) { index ->
            val (id, note) = notes[index]
            NoteCard(
                title = note.first,
                content = note.second,
                onClick = { onEditNote(id) }
            )
        }
    }
}

@Composable
fun NoteCard(title: String, content: String, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
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
@Composable
fun TaskList(tasks: List<Pair<Int, Pair<String, String>>>, onEditTask: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tasks.size) { index ->
            val (id, task) = tasks[index]
            TaskCard(
                title = task.first,
                content = task.second,
                onClick = { onEditTask(id) }
            )
        }
    }
}

@Composable
fun TaskCard(title: String, content: String, onClick: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDFFFD6) // Color diferente para distinguir las tareas
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
fun Frases(): Pair<Int, String> {
    // Crear el HashMap
    val myMap: HashMap<Int, String> = HashMap()

    // Agregar elementos
    myMap[1] = "El hombre que domina al hombre es admirable, el hombre que se domina a si mismo es invencible "
    myMap[2] = "Todos somos genios, pero si gusgas a un pez por su habilidad de escalar un árbol, vivirá su vida entera creyendo que es estúpido"
    myMap[3] = "Conócete a ti mismo, conoce el terreno, el clima, al enemigo y podrás librar cien batallas sin correr ningún riesgo de derrota"
    myMap[4] = "Todos nuestros sueños se pueden convertir en realidad  si tenemos el coraje de seguirlos"


    // Obtener una lista de valores del HashMap
    val valores = myMap.values.toList() // Convertir a lista

    // Seleccionar un valor aleatorio
    val indiceAleatorio = Random.nextInt(1, valores.size + 1) // Evita índice 0
    val valorAleatorio = valores[indiceAleatorio - 1]
    // Devolver el valor aleatorio
    return Pair(indiceAleatorio, valorAleatorio)
}
fun Autores(indice:Int ): String {
    // Crear el HashMap
    val myMap: HashMap<Int, String> = HashMap()
    // Agregar elementos
    myMap[1] = "Friedrich Nietzsche"
    myMap[2] = "Albert Einstein"
    myMap[3] = "Sun Tzu"
    myMap[4] = "Walt Disney"

    return myMap[indice] ?: "Autor no encontrado"
}

