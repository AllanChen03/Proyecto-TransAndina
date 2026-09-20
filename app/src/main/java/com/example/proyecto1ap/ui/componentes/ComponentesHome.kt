package com.example.proyecto1ap.ui.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.mantenimiento.ResumenVehiculo
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoSecundario

@Composable
fun MiniTarjetaEstadistica(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Superficie),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.dp, AzulPrimario.copy(alpha = 0.4f), CircleShape)
                    .padding(9.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icono,
                    contentDescription = null,
                    tint = AzulPrimario,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
                Text(
                    text = valor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TarjetaColeccionVehiculos(
    titulo: String,
    vehiculos: List<ResumenVehiculo>,
    modifier: Modifier = Modifier,
    colorBorde: androidx.compose.ui.graphics.Color = AzulPrimario
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colorBorde, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = vehiculos.size.toString(),
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }
            if (vehiculos.isEmpty()) {
                Text(
                    text = "Sin vehículos por ahora",
                    color = TextoSecundario,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            } else {
                vehiculos.forEachIndexed { index, v ->
                    if (index > 0) {
                        androidx.compose.foundation.layout.Spacer(
                            Modifier.padding(top = 10.dp)
                        )
                    }
                    Text(
                        text = "${v.placa} · ${v.descripcion}",
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF374151),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}