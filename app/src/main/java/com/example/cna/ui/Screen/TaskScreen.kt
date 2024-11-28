package com.example.cna.ui.Screen

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import com.example.cna.componentes.DatePickerFecha
import com.example.cna.componentes.VideoCaptureButton
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cna.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun taskScreen(
    state: TareaState,
    tareaid: Int?,
    onEvent: (TareaEvent) -> Unit,
) {
    val viewModel: TaskViewModel = hiltViewModel()
    LaunchedEffect(tareaid) {
        tareaid?.let { viewModel.loadTaskById(it) }
    }

    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? = remember { null }
    val (previewUri, setPreviewUri) = remember { mutableStateOf<String?>(null) }
    val (isVideo, setIsVideo) = remember { mutableStateOf(false) }

    fun playAudio(uri: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(uri))
                setOnPreparedListener { it.start() }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayback", "Error al reproducir el audio", e)
        }
    }

    if (previewUri != null) {
        FullScreenPreview(
            uri = previewUri,
            isVideo = isVideo,
            onDismiss = { setPreviewUri(null) }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
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
                        onEvent(TareaEvent.Save)
                        onEvent(TareaEvent.NavigateBack)
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(id = R.string.save_task)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        onEvent(TareaEvent.DeleteTask)
                        onEvent(TareaEvent.NavigateBack)
                    },
                    containerColor = Color(0xFFF44336)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.delete_task)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo de título
            item {
                BasicTextField(
                    value = state.title,
                    onValueChange = { onEvent(TareaEvent.TitleChange(it)) },
                    textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (state.title.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.new_task_title),
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Campo de contenido
            item {
                BasicTextField(
                    value = state.content,
                    onValueChange = { onEvent(TareaEvent.ContentChange(it)) },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    decorationBox = { innerTextField ->
                        if (state.content.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.write_your_task),
                                color = Color.Gray
                            )
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
                                onEvent(TareaEvent.AddImage(it.toString()))
                            }
                        }
                    })
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
            }
            // Selección de fecha
            item {
                DatePickerFecha { selectedCalendar ->
                    checkNotificationPermission(context) {
                        scheduleNotification(
                            context = context.applicationContext,
                            calendar = selectedCalendar,
                            taskTitle = state.title
                        )
                    }
                }
            }

            val validAudios = state.AudioUris.filter { it.isNotEmpty() }
            if (validAudios.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.audio),
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(validAudios) { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uri.split("/").last(), // Muestra solo el nombre del archivo
                            style = TextStyle(fontSize = 16.sp, color = Color.Black),
                            modifier = Modifier.padding(8.dp)
                        )
                        Row {
                            Button(
                                onClick = { playAudio(uri) },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(text = stringResource(id = R.string.play))
                            }
                            IconButton(
                                onClick = { onEvent(TareaEvent.RemoveAudio(uri)) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(id = R.string.delete_audio),
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }

            val validImages = state.imageUris.filter { it.isNotEmpty() }
            if (validImages.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.saved_images),
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(validImages) { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = stringResource(id = R.string.saved_images),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    setPreviewUri(uri)
                                    setIsVideo(false)
                                },
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { onEvent(TareaEvent.RemoveImage(uri)) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.delete_image),
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            val validVideos = state.videosUris.filter { it.isNotEmpty() }
            if (validVideos.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.saved_videos),
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(validVideos) { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                    ) {
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
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { onEvent(TareaEvent.RemoveVideo(uri)) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.delete_video),
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}


// Constante para identificar la solicitud de permiso
const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

fun checkNotificationPermission(context: Context, onPermissionGranted: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionGranted()
        } else {
            val activity = context as? android.app.Activity ?: return
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    } else {
        // Los permisos para notificaciones no son necesarios en versiones anteriores
        onPermissionGranted()
    }
}

fun scheduleNotification(context: Context, calendar: java.util.Calendar, taskTitle: String) {
    try {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("taskTitle", taskTitle)
        }

        val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            pendingIntentFlags
        )

        val alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("Notification", "Notificación programada con éxito para: ${calendar.time}")

    } catch (e: Exception) {
        Log.e("Notification", "Error al programar la notificación", e)
    }

    @Composable
    fun FullScreenPreview(
        uri: String,
        isVideo: Boolean,
        onDismiss: () -> Unit
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                if (isVideo) {
                    AndroidView(
                        factory = { ctx ->
                            androidx.media3.ui.PlayerView(ctx).apply {
                                val exoPlayer = androidx.media3.exoplayer.ExoPlayer.Builder(ctx).build().apply {
                                    setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
                                    prepare()
                                    playWhenReady = true
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
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
