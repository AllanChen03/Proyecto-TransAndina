package com.example.proyecto1ap.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.ui.theme.AmarilloFondo
import com.example.proyecto1ap.ui.theme.AmarilloTexto
import com.example.proyecto1ap.ui.theme.AzulClaro
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.RojoFondo
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.ui.theme.VerdeFondo
import com.example.proyecto1ap.ui.theme.VerdeTexto

@Composable
fun TarjetaSeccion(
    modifier: Modifier = Modifier,
    titulo: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (titulo != null) {
                Text(
                    text = titulo,
                    fontSize = 13.sp,
                    color = TextoSecundario
                )
                Spacer(Modifier.height(12.dp))
            }
            content()
        }
    }
}

@Composable
fun FilaDato(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            color = TextoSecundario,
            fontSize = 15.sp
        )
        Text(
            text = valor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}
enum class EstadoVisual {
    OK,
    ADVERTENCIA,
    CRITICO,
    NEUTRO
}


@Composable
fun ChipEstado(
    texto: String,
    estado: EstadoVisual,
    modifier: Modifier = Modifier
) {
    val fondo = when (estado) {
        EstadoVisual.OK -> VerdeFondo
        EstadoVisual.ADVERTENCIA -> AmarilloFondo
        EstadoVisual.CRITICO -> RojoFondo
        EstadoVisual.NEUTRO -> AzulClaro
    }
    val colorTexto = when (estado) {
        EstadoVisual.OK -> VerdeTexto
        EstadoVisual.ADVERTENCIA -> AmarilloTexto
        EstadoVisual.CRITICO -> RojoTexto
        EstadoVisual.NEUTRO -> AzulPrimario
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = colorTexto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}