package com.example.cna.ui.Screen

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cna.componentes.AudioRecorderButton
import com.example.cna.componentes.CameraButton
import com.example.cna.componentes.VideoCaptureButton

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

    // MediaPlayer para reproducción de audio
    var mediaPlayer: MediaPlayer? = remember { null }
    val context = LocalContext.current

    fun playAudio(uri: String) {
        try {
            // Libera el MediaPlayer actual si está en uso
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(uri))
                setOnPreparedListener {
                    it.start() // Comienza la reproducción cuando el audio está listo
                }
                prepareAsync() // Carga el archivo de audio de forma asincrónica
            }
        } catch (e: Exception) {
            Log.e("AudioPlayback", "Error al reproducir el audio", e)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Nota") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(NoteEvent.NavigateBack) }) {
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
        },
        floatingActionButton = {
            Column(
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = {
                        onEvent(NoteEvent.Save)
                        onEvent(NoteEvent.NavigateBack)
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar Nota"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        onEvent(NoteEvent.DeleteNote)
                        onEvent(NoteEvent.NavigateBack)
                    },
                    containerColor = Color(0xFFF44336)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar Nota"
                    )
                }
            }
        }
    )  { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de Título
            item {
                Text(
                    text = "Título",
                    style = TextStyle(fontSize = 18.sp, color = Color.Black),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BasicTextField(
                    value = state.title,
                    onValueChange = { onEvent(NoteEvent.TitleChange(it)) },
                    textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, MaterialTheme.shapes.small)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (state.title.isEmpty()) {
                            Text(text = "Escribe el título...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }

            // Campo de Contenido
            item {
                Text(
                    text = "Contenido",
                    style = TextStyle(fontSize = 18.sp, color = Color.Black),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BasicTextField(
                    value = state.content,
                    onValueChange = { onEvent(NoteEvent.ContentChange(it)) },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray, MaterialTheme.shapes.small)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (state.content.isEmpty()) {
                            Text(text = "Escribe tu nota aquí...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }

            // Botones para multimedia
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CameraButton(onImagesCaptured = { uri ->
                        uri?.let {
                            if (!state.imageUris.contains(it.toString())) {
                                onEvent(NoteEvent.AddImage(it.toString()))
                            }
                        }
                    })
                    AudioRecorderButton(onAudiosCaptured = { uris ->
                        uris.forEach { uri ->
                            if (!state.AudioUris.contains(uri.toString())) {
                                onEvent(NoteEvent.AddAudio(uri.toString()))
                            }
                        }
                    })
                    VideoCaptureButton(onVideoCaptured = { uri ->
                        uri?.let {
                            if (!state.videosUris.contains(it.toString())) {
                                onEvent(NoteEvent.AddVideo(it.toString()))
                            }
                        }
                    })
                }
            }

            // Mostrar Imágenes
            if (state.imageUris.isNotEmpty()) {
                item {
                    Text(
                        text = "Imágenes Guardadas",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(state.imageUris) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagen guardada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Mostrar Videos
            if (state.videosUris.isNotEmpty()) {
                item {
                    Text(
                        text = "Videos Guardados",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(state.videosUris) { uri ->
                    AndroidView(
                        factory = { ctx ->
                            androidx.media3.ui.PlayerView(ctx).apply {
                                val exoPlayer = androidx.media3.exoplayer.ExoPlayer.Builder(ctx).build().apply {
                                    setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
                                    prepare()
                                    playWhenReady = false
                                }
                                player = exoPlayer
                                addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                                    override fun onViewAttachedToWindow(view: View) {}
                                    override fun onViewDetachedFromWindow(view: View) {
                                        exoPlayer.release()
                                    }
                                })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                    )
                }
            }

            // Mostrar Audios
            if (state.AudioUris.isNotEmpty()) {
                item {
                    Text(
                        text = "Audios Guardados",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(state.AudioUris) { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Audio",
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.padding(8.dp)
                        )
                        Button(onClick = { playAudio(uri) }) {
                            Text(text = "Reproducir")
                        }
                    }
                }
            }
        }
    }
}