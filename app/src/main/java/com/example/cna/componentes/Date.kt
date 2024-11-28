package com.example.cna.componentes

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun DatePickerFecha(onDateTimeSelected: (Calendar) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column {
        Button(onClick = {
            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                date = "$dayOfMonth/${month + 1}/$year"

                TimePickerDialog(context, { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    time = "$hourOfDay:$minute"
                    onDateTimeSelected(calendar) // Retorna la fecha y hora seleccionadas
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }) {
            Text("Programar Notificacion")
        }

        Text(text = "Fecha seleccionada: $date")
        Text(text = "Hora seleccionada: $time")
    }
}
