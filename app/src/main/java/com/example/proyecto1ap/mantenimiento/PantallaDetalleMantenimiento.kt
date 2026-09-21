package com.example.proyecto1ap.mantenimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.FilaDato
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleMantenimiento(
    mantenimientoId: Long,
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: DetalleMantenimiento = viewModel(
        key = mantenimientoId.toString(),
        factory = DetalleMantenimientoFactory(mantenimientoId)
    )
) {
    val s by vm.state.collectAsState()
    val m = s.mantenimiento
    var fotoAmpliada by remember { mutableStateOf<String?>(null) }

    fotoAmpliada?.let { url ->
        Dialog(onDismissRequest = { fotoAmpliada = null }) {
            AsyncImage(
                model = url,
                contentDescription = "Evidencia ampliada",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { fotoAmpliada = null },
                contentScale = ContentScale.Fit
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del mantenimiento", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        when {
            s.cargando -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            s.error != null || m == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(s.error ?: "Mantenimiento no encontrado", color = RojoTexto)
            }

            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FondoApp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                TarjetaSeccion {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = m.vehiculoPlaca,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            Text(
                                text = m.vehiculoDescripcion,
                                fontSize = 14.sp,
                                color = TextoSecundario
                            )
                        }
                        ChipEstado(
                            texto = m.tipo.etiqueta,
                            estado = if (m.tipo == TipoMantenimiento.PREVENTIVO)
                                EstadoVisual.OK else EstadoVisual.ADVERTENCIA
                        )
                    }
                }

                TarjetaSeccion(titulo = "Información del servicio") {
                    FilaDato("Fecha", m.fecha)
                    FilaDato("Categoría", m.categoriaServicio ?: "—")
                    FilaDato(
                        "Kilometraje",
                        m.kilometraje?.let { "%,d km".format(it) } ?: "—"
                    )
                    FilaDato(
                        "Costo",
                        m.costo?.let { "₡%,.0f".format(it) } ?: "No registrado"
                    )
                    FilaDato("Realizado por", m.mecanicoNombre ?: "—")
                    if (!m.taller.isNullOrBlank()) {
                        FilaDato("Taller externo", m.taller)
                    }
                }

                TarjetaSeccion(titulo = "Descripción del trabajo") {
                    Text(
                        text = m.descripcion,
                        fontSize = 15.sp,
                        color = TextoPrincipal
                    )
                }

                TarjetaSeccion(titulo = "Evidencia fotográfica") {
                    if (s.fotos.isEmpty()) {
                        Text(
                            text = "Sin fotos registradas",
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.fotos) { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Evidencia",
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { fotoAmpliada = url },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Tocá una foto para ampliarla",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}