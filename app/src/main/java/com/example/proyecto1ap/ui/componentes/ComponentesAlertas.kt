package com.example.proyecto1ap.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.mantenimiento.AlertaListado
import com.example.proyecto1ap.ui.theme.AmarilloTexto
import com.example.proyecto1ap.ui.theme.RojoFondo
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

@Composable
fun BannerAlertas(
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RojoFondo)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.NotificationImportant,
            contentDescription = null,
            tint = RojoTexto,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Tenés $total alertas activas",
                color = RojoTexto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Requieren revisión inmediata",
                color = RojoTexto,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun EncabezadoGrupoAlerta(
    nombreGrupo: String,
    cantidad: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = nombreGrupo.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoPrincipal
        )
        Text(
            text = cantidad.toString(),
            fontSize = 12.sp,
            color = TextoSecundario
        )
    }
}

@Composable
fun TarjetaAlerta(
    alerta: AlertaListado,
    modifier: Modifier = Modifier
) {
    val colorTira = when (alerta.prioridad.uppercase()) {
        "URGENTE", "VENCIDO" -> RojoTexto
        "ADVERTENCIA" -> AmarilloTexto
        else -> TextoSecundario
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Superficie)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(width = 4.dp, height = 90.dp)
                .background(colorTira)
        )
        Row(modifier = Modifier.padding(start = 18.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = alerta.vehiculoPlaca ?: "General",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    ChipPrioridad(alerta.prioridad)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = alerta.titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = alerta.mensaje,
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ChipPrioridad(prioridad: String) {
    val (fondo, texto) = when (prioridad.uppercase()) {
        "URGENTE", "VENCIDO" -> RojoFondo to RojoTexto
        "ADVERTENCIA" -> androidx.compose.ui.graphics.Color(0xFFFFF3C4) to AmarilloTexto
        else -> androidx.compose.ui.graphics.Color(0xFFE9ECEF) to TextoSecundario
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(fondo)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = prioridad.uppercase(),
            color = texto,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}