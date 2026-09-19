package com.example.proyecto1ap.ui.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.ui.theme.AmarilloFondo
import com.example.proyecto1ap.ui.theme.AmarilloTexto
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.RojoBorde
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

/**
 * Campo de texto con etiqueta arriba. Base de todos los formularios.
 */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    lineasMaximas: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            color = TextoSecundario,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = TextoSecundario, fontSize = 15.sp)
            },
            isError = error != null,
            singleLine = lineasMaximas == 1,
            maxLines = lineasMaximas,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulPrimario,
                unfocusedBorderColor = Borde,
                errorBorderColor = RojoBorde,
                focusedContainerColor = Superficie,
                unfocusedContainerColor = Superficie
            )
        )
    }
}

/**
 * Desplegable para opciones fijas (rol, combustible, conductor).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelector(
    etiqueta: String,
    valorSeleccionado: String?,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Seleccionar"
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            color = TextoSecundario,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido }
        ) {
            OutlinedTextField(
                value = valorSeleccionado ?: "",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                placeholder = {
                    Text(placeholder, color = TextoSecundario, fontSize = 15.sp)
                },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulPrimario,
                    unfocusedBorderColor = Borde,
                    focusedContainerColor = Superficie,
                    unfocusedContainerColor = Superficie
                )
            )
            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccion(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Campo de fecha de solo lectura. El onClick debe abrir el DatePicker.
 */
@Composable
fun CampoFecha(
    etiqueta: String,
    fechaTexto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            color = TextoSecundario,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Borde, RoundedCornerShape(8.dp))
                .background(Superficie)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fechaTexto,
                fontSize = 15.sp,
                color = TextoPrincipal
            )
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Botón azul de ancho completo. Se atenúa cuando está deshabilitado.
 */
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulPrimario,
            disabledContainerColor = AzulPrimario.copy(alpha = 0.5f),
            disabledContentColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Aviso amarillo para validaciones (ej: kilometraje menor al último).
 */
@Composable
fun MensajeAdvertencia(
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AmarilloFondo)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = AmarilloTexto,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = texto,
            color = AmarilloTexto,
            fontSize = 13.sp
        )
    }
}