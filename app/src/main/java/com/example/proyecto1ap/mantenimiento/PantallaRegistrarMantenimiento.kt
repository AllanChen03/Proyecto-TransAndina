package com.example.proyecto1ap.mantenimiento

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoEvidenciaFotos
import com.example.proyecto1ap.ui.componentes.CampoFechaSelector
import com.example.proyecto1ap.ui.componentes.CampoSelector
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.componentes.SelectorTipo
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.RojoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarMantenimiento(
    onVolver: () -> Unit = {},
    onGuardado: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: RegistrarMantenimiento = viewModel()
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val hayDatos = s.vehiculoId != null ||
            s.fecha.isNotBlank() ||
            s.categoriaServicio != null ||
            s.kilometraje.isNotBlank() ||
            s.descripcion.isNotBlank() ||
            s.costo.isNotBlank() ||
            s.taller != null ||
            s.fotos.isNotEmpty()

    fun intentarSalir() {
        if (hayDatos && !s.exito) mostrarDialogo = true else onVolver()
    }

    BackHandler { intentarSalir() }

    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    LaunchedEffect(s.exito) {
        if (s.exito) onGuardado()
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Descartar registro") },
            text = { Text("Perderás lo ingresado. ¿Querés salir de todas formas?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    onVolver()
                }) {
                    Text("Descartar", color = RojoTexto)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Seguir editando")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Registrar mantenimiento") },
                navigationIcon = {
                    IconButton(onClick = { intentarSalir() }) {
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
                placeholder = "Ej: 45000",
                tipoTeclado = KeyboardType.Number
            )

            CampoTexto(
                "Descripción", s.descripcion, vm::onDescripcion,
                placeholder = "Detalle del trabajo realizado",
                lineasMaximas = 4
            )

            CampoTexto(
                "Costo (opcional)", s.costo, vm::onCosto,
                placeholder = "Ej: 85000",
                tipoTeclado = KeyboardType.Decimal,
                prefijo = "₡"
            )

            CampoSelector(
                etiqueta = "Taller o mecánico",
                valorSeleccionado = s.taller,
                opciones = TALLERES,
                onSeleccion = vm::onTaller,
                placeholder = "Seleccionar taller"
            )

            CampoEvidenciaFotos(
                fotos = s.fotos,
                onAgregar = vm::onFotosAgregar,
                onQuitar = vm::onQuitarFoto
            )

            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Borde)
            Spacer(Modifier.height(4.dp))

            BotonPrimario(
                texto = if (s.guardando) "Guardando mantenimiento..." else "Guardar mantenimiento",
                onClick = vm::guardar,
                habilitado = s.puedeGuardar
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}