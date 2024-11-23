package com.example.cna.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun VideoCaptureButton(onVideoCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    val allPermissionsGranted = listOf(Manifest.permission.CAMERA).all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    val tempVideoUri = remember {
        try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                File.createTempFile("VID_", ".mp4", context.cacheDir)
            )
        } catch (e: Exception) {
            null
        }
    }

    val openVideoRecorder = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempVideoUri != null) {
            onVideoCaptured(tempVideoUri)
        }
    }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        if (!permissionsResult.all { it.value }) {
            Toast.makeText(context, "Se necesitan permisos para grabar video", Toast.LENGTH_LONG).show()
        }
    }

    if (allPermissionsGranted) {
        Button(onClick = { tempVideoUri?.let { openVideoRecorder.launch(it) } }) {
            Icon(Icons.Filled.Videocam, contentDescription = "Abrir cámara para video")
        }
    } else {
        Button(onClick = { requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA)) }) {
            Icon(Icons.Filled.Videocam, contentDescription = "Solicitar permisos para video")
        }
    }
}
