package com.example.cna.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun AudioRecorderButton(onAudiosCaptured: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    val audioUris = remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isRecording by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) } // Control de permisos
    val mediaRecorder = remember { MediaRecorder() }
    val audioFile = remember {
        File(context.externalCacheDir, "audio_${System.currentTimeMillis()}.3gp")
    }

    // Función para iniciar la grabación
    fun startRecording() {
        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(audioFile.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
            isRecording = true
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error al iniciar grabación", e)
        }
    }

    // Función para detener la grabación
    fun stopRecording() {
        try {
            mediaRecorder.apply {
                stop()
                reset()
            }
            isRecording = false
            audioUris.value = audioUris.value + Uri.fromFile(audioFile)
            onAudiosCaptured(audioUris.value)
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error al detener grabación", e)
        }
    }

    // Manejador de permisos
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted && !isRecording) {
            startRecording()
        } else if (!isGranted) {
            Toast.makeText(context, "Permiso para grabar audio denegado", Toast.LENGTH_LONG).show()
        }
    }

    // UI del botón
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true
                if (isRecording) stopRecording() else startRecording()
            } else {
                // Solicitar permiso
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Detener grabación" else "Iniciar grabación",
                tint = if (isRecording) Color.Red else Color.Black
            )
        }
    }

    // Liberar el MediaRecorder al salir del Composable
    DisposableEffect(Unit) {
        onDispose {
            mediaRecorder.release()
        }
    }
}

