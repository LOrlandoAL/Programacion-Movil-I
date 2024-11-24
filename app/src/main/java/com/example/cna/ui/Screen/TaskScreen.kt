package com.example.cna.ui.Screen

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cna.componentes.AudioRecorderButton
import com.example.cna.componentes.CameraButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.cna.componentes.VideoCaptureButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun taskScreen(
    state: TareaState,
    tareaid: Int?,
    onEvent: (TareaEvent) -> Unit,
) {
    val viewModel: TaskViewModel = hiltViewModel()
    LaunchedEffect(tareaid) {
        tareaid?.let { viewModel.loadNoteById(it) }
    }
    // MediaPlayer para reproducción
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
                title = { Text("C.N.A") },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(TareaEvent.NavigateBack) },
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
                    onValueChange = { onEvent(TareaEvent.TitleChange(it)) },
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
                            onEvent(TareaEvent.AddImage(it.toString()))
                        }
                    }
                })
                // Botón de grabación de audio
                AudioRecorderButton(onAudiosCaptured = { uris ->
                    uris.forEach { uri ->
                        if (!state.AudioUris.contains(uri.toString())) {
                            onEvent(TareaEvent.AddAudio(uri.toString()))
                        }
                    }
                })
                VideoCaptureButton(onVideoCaptured = { uri ->
                    uri?.let {
                        if (!state.videosUris.contains(it.toString())) {
                            onEvent(TareaEvent.AddVideo(it.toString()))
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

                // Mostrar lista de audios
                Text(
                    text = "Audios guardados:",
                    style = TextStyle(fontSize = 18.sp, color = Color.Black),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            // Mostrar imágenes capturadas usando AsyncImage
            items(state.videosUris.size) { index ->
                Spacer(modifier = Modifier.height(16.dp))
                val videoUri = state.videosUris[index]

                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            val exoPlayer = ExoPlayer.Builder(ctx).build().apply {
                                setMediaItem(androidx.media3.common.MediaItem.fromUri(videoUri))
                                prepare()
                                playWhenReady = false
                            }
                            player = exoPlayer

                            // Libera recursos del ExoPlayer cuando la vista se destruye
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
                )
            }
            items(state.AudioUris.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Audio ${index + 1}",
                        style = TextStyle(fontSize = 16.sp, color = Color.Black),
                        modifier = Modifier.padding(8.dp)
                    )
                    Button(onClick = {
                        playAudio(state.AudioUris[index]) // Reproduce el audio
                    }) {
                        Text(text = "Reproducir")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contenido
                BasicTextField(
                    value = state.content,
                    onValueChange = { onEvent(TareaEvent.ContentChange(it)) },
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
                    onClick = { onEvent(TareaEvent.SaveTaskAndNavigateBack) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Guardar Tarea")
                }
            }
        }
    }
}