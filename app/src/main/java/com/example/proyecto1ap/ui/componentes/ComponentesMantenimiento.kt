package com.example.proyecto1ap.ui.componentes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.proyecto1ap.mantenimiento.MantenimientoListado
import com.example.proyecto1ap.mantenimiento.TipoMantenimiento
import com.example.proyecto1ap.mantenimiento.formatearCosto
import com.example.proyecto1ap.mantenimiento.formatearFecha
import com.example.proyecto1ap.ui.theme.AzulClaro
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.ui.theme.VerdeFondo
import com.example.proyecto1ap.ui.theme.VerdeTexto

@Composable
fun TarjetaRegistroMantenimiento(
    registro: MantenimientoListado,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Superficie)
            .border(1.dp, Borde, RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatearFecha(registro.fecha),
                fontSize = 13.sp,
                color = TextoSecundario
            )
            val fondo = if (registro.tipo == TipoMantenimiento.PREVENTIVO)
                VerdeFondo else AzulClaro
            val colorTexto = if (registro.tipo == TipoMantenimiento.PREVENTIVO)
                VerdeTexto else AzulPrimario

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(fondo)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = registro.tipo.etiqueta.uppercase(),
                    color = colorTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = registro.titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${registro.vehiculoPlaca} · ${registro.vehiculoDescripcion}",
            fontSize = 14.sp,
            color = TextoPrincipal
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Borde)
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registró: ${registro.mecanico ?: "-"}",
                fontSize = 13.sp,
                color = TextoSecundario,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatearCosto(registro.costo),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulPrimario
            )
        }
    }
}

@Composable
fun CampoEvidenciaFotos(
    fotos: List<Uri>,
    onAgregar: (List<Uri>) -> Unit,
    onQuitar: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> onAgregar(uris) }
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Evidencia fotográfica",
            fontSize = 13.sp,
            color = TextoSecundario,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fotos.isEmpty()) {
                item {
                    Text(
                        text = "Sin fotos agregadas (opcional)",
                        fontSize = 14.sp,
                        color = TextoSecundario,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(fotos, key = { it.toString() }) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Borde, RoundedCornerShape(10.dp))
                        )
                        IconButton(
                            onClick = { onQuitar(uri) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(26.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Quitar foto",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, AzulPrimario, RoundedCornerShape(10.dp))
                        .clickable {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = AzulPrimario,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Agregar",
                            color = AzulPrimario,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}