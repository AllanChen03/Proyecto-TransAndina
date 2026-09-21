package com.example.proyecto1ap.mantenimiento

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoEvidenciaFotos
import com.example.proyecto1ap.ui.componentes.CampoFechaSelector
import com.example.proyecto1ap.ui.componentes.CampoSelector
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.componentes.SelectorTipo
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarMantenimiento(
    mantenimientoId: Long,
    onVolver: () -> Unit = {},
    onGuardado: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vm: EditarMantenimiento = viewModel(
        key = "editar_$mantenimientoId",
        factory = EditarMantenimientoFactory(
            context.applicationContext as Application,
            mantenimientoId
        )
    )

    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var fotoAEliminar by remember { mutableStateOf<FotoEvidencia?>(null) }

    BackHandler { onVolver() }

    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    LaunchedEffect(s.guardadoExitoso) {
        if (s.guardadoExitoso) {
            vm.limpiarGuardado()
            onGuardado()
        }
    }
    fotoAEliminar?.let { foto ->
        AlertDialog(
            onDismissRequest = { fotoAEliminar = null },
            title = { Text("Eliminar foto") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminarFotoExistente(foto)
                    fotoAEliminar = null
                }) {
                    Text("Eliminar", color = RojoTexto)
                }
            },
            dismissButton = {
                TextButton(onClick = { fotoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Editar mantenimiento") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->

        if (s.cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SelectorTipo(
                opciones = TipoMantenimiento.listado(),
                seleccionado = s.tipo.etiqueta,
                onSeleccion = { etiqueta ->
                    TipoMantenimiento.entries.firstOrNull { it.etiqueta == etiqueta }
                        ?.let(vm::onTipo)
                }
            )

            CampoSelector(
                etiqueta = "Vehículo",
                valorSeleccionado = s.vehiculos.firstOrNull { it.id == s.vehiculoId }?.let {
                    "${it.placa} (${it.marca} ${it.modelo})"
                },
                opciones = s.vehiculos.map { "${it.placa} (${it.marca} ${it.modelo})" },
                onSeleccion = { desc ->
                    s.vehiculos.firstOrNull {
                        "${it.placa} (${it.marca} ${it.modelo})" == desc
                    }?.id?.let(vm::onVehiculo)
                },
                placeholder = "Seleccionar vehículo"
            )

            CampoFechaSelector(
                etiqueta = "Fecha de servicio",
                valor = s.fecha,
                onFechaSeleccionada = vm::onFecha
            )

            CampoSelector(
                etiqueta = "Categoría del servicio",
                valorSeleccionado = etiquetaCategoria(s.categoriaServicio),
                opciones = CATEGORIAS_SERVICIO.map { it.second },
                onSeleccion = { etiqueta ->
                    vm.onCategoriaServicio(
                        CATEGORIAS_SERVICIO.first { it.second == etiqueta }.first
                    )
                },
                placeholder = "Seleccionar categoría"
            )

            CampoTexto(
                "Kilometraje", s.kilometraje, vm::onKilometraje,
                tipoTeclado = KeyboardType.Number
            )

            Column {
                CampoTexto(
                    "Descripción", s.descripcion, vm::onDescripcion,
                    lineasMaximas = 4
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (s.descripcion.trim().length >= MIN_DESCRIPCION)
                        "${s.descripcion.trim().length} caracteres"
                    else
                        "Mínimo $MIN_DESCRIPCION caracteres",
                    fontSize = 12.sp,
                    color = if (s.descripcion.trim().length < MIN_DESCRIPCION)
                        RojoTexto else TextoSecundario
                )
            }

            CampoTexto(
                "Costo (opcional)", s.costo, vm::onCosto,
                tipoTeclado = KeyboardType.Decimal,
                prefijo = "₡"
            )

            CampoTexto(
                etiqueta = "Taller externo (opcional)",
                valor = s.taller,
                onValorChange = vm::onTaller
            )

            // ---------- Fotos existentes ----------

            if (s.fotosExistentes.isNotEmpty()) {
                Column {
                    Text(
                        text = "Fotos guardadas",
                        fontSize = 13.sp,
                        color = TextoSecundario,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(s.fotosExistentes, key = { it.id }) { foto ->
                            Box {
                                AsyncImage(
                                    model = foto.url,
                                    contentDescription = "Evidencia",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                IconButton(
                                    onClick = { fotoAEliminar = foto },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(26.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.55f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ---------- Fotos nuevas ----------

            CampoEvidenciaFotos(
                fotos = s.fotosNuevas,
                onAgregar = vm::onFotosAgregar,
                onQuitar = vm::onQuitarFotoNueva
            )

            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Borde)
            Spacer(Modifier.height(4.dp))

            if (s.faltantes.isNotEmpty()) {
                Text(
                    text = "Falta completar: ${s.faltantes.joinToString(", ")}",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }

            BotonPrimario(
                texto = if (s.guardando) "Guardando..." else "Guardar cambios",
                onClick = vm::guardar,
                habilitado = s.puedeGuardar
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}