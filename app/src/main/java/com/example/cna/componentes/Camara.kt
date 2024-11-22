package com.example.cna.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File

@Composable
fun CameraButton(onImagesCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    val allPermissionsGranted = listOf(Manifest.permission.CAMERA).all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    val tempFileUri = remember {
        try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                File.createTempFile("IMG_", ".jpg", context.cacheDir)
            )
        } catch (e: Exception) {
            null
        }
    }

    val openCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempFileUri != null) {
            onImagesCaptured(tempFileUri)
        }
    }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        if (!permissionsResult.all { it.value }) {
            Toast.makeText(context, "Se necesitan permisos para usar la cámara", Toast.LENGTH_LONG).show()
        }
    }

    if (allPermissionsGranted) {
        Button(onClick = { tempFileUri?.let { openCamera.launch(it) } }) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Abrir cámara")
        }
    } else {
        Button(onClick = { requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA)) }) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Solicitar permisos")
        }
    }
}



