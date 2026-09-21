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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleMantenimiento(
    mantenimientoId: Long,
    esEncargado: Boolean = false,
    onVolver: () -> Unit = {},
    onEditar: (Long) -> Unit = {},
    onEliminado: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: DetalleMantenimiento = viewModel(
        key = mantenimientoId.toString(),
        factory = DetalleMantenimientoFactory(mantenimientoId)
    )
) {
    val s by vm.state.collectAsState()
    val m = s.mantenimiento
    val snackbar = remember { SnackbarHostState() }

    var fotoAmpliada by remember { mutableStateOf<String?>(null) }
    var confirmarBorrado by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargar() }

    LaunchedEffect(s.eliminado) {
        if (s.eliminado) onEliminado()
    }

    LaunchedEffect(s.error) {
        s.error?.let {
            snackbar.showSnackbar(it)
            vm.limpiarError()
        }
    }

    // Foto ampliada
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

    // Confirmación de borrado
    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Eliminar mantenimiento") },
            text = {
                Text(
                    "Se eliminará el registro y todas sus fotos. " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrado = false
                    vm.eliminar()
                }) {
                    Text("Eliminar", color = RojoTexto)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) {
                    Text("Cancelar")
                }
            }
        )
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
                actions = {
                    if (esEncargado && m != null) {
                        IconButton(onClick = { onEditar(mantenimientoId) }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar",
                                tint = AzulPrimario
                            )
                        }
                        IconButton(
                            onClick = { confirmarBorrado = true },
                            enabled = !s.eliminando
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar",
                                tint = RojoTexto
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->

        when {
            s.cargando -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            m == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Mantenimiento no encontrado", color = RojoTexto)
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
                    FilaDato("Registrado por", m.mecanicoNombre ?: "—")
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