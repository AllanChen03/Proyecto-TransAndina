package com.example.proyecto1ap.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import java.time.Instant
import java.time.ZoneId

@Composable
fun SelectorTipo(
    opciones: List<String>,
    seleccionado: String,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tipo de mantenimiento",
            fontSize = 13.sp,
            color = TextoSecundario,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            opciones.forEach { opcion ->
                val esSeleccionado = opcion == seleccionado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (esSeleccionado) AzulPrimario else Superficie
                        )
                        .border(
                            width = 1.dp,
                            color = if (esSeleccionado) AzulPrimario else Borde,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSeleccion(opcion) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = esSeleccionado,
                            onClick = { onSeleccion(opcion) }
                        )
                        Text(
                            text = opcion,
                            color = if (esSeleccionado) Superficie else TextoPrincipal,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoRangoFechas(
    abierto: Boolean,
    desde: String,
    hasta: String,
    onCambioDesde: (String) -> Unit,
    onCambioHasta: (String) -> Unit,
    onLimpiar: () -> Unit,
    onCerrar: () -> Unit
) {
    var campoVisible by remember { mutableStateOf("") }

    if (campoVisible.isNotEmpty()) {
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = runCatching {
                Instant.parse("${if (campoVisible == "desde") desde else hasta}T00:00:00Z")
                    .toEpochMilli()
            }.getOrNull()
        )
        DatePickerDialog(
            onDismissRequest = { campoVisible = "" },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { millis ->
                        val fecha = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .toString()
                        if (campoVisible == "desde") onCambioDesde(fecha) else onCambioHasta(fecha)
                    }
                    campoVisible = ""
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { campoVisible = "" }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estado)
        }
    }

    if (abierto && campoVisible.isEmpty()) {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("Rango de fechas") },
            text = {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    FilaRango("Desde", desde) { campoVisible = "desde" }
                    FilaRango("Hasta", hasta) { campoVisible = "hasta" }
                }
            },
            confirmButton = {
                TextButton(onClick = onCerrar) { Text("Listo") }
            },
            dismissButton = {
                TextButton(onClick = onLimpiar) { Text("Limpiar") }
            }
        )
    }
}

@Composable
private fun FilaRango(
    etiqueta: String,
    valor: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFD5DBE1), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, color = TextoSecundario, fontSize = 14.sp)
        Text(
            text = valor.ifBlank { "Seleccionar" },
            color = if (valor.isBlank()) TextoSecundario else TextoPrincipal,
            fontSize = 14.sp
        )
    }
}