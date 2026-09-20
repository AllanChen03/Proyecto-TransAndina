package com.example.proyecto1ap.Vehiculo

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
import com.example.proyecto1ap.ui.componentes.CampoFechaSelector
import com.example.proyecto1ap.ui.componentes.CampoSelector
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.RojoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVehiculoRegistro(
    onVolver: () -> Unit = {},
    onGuardado: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: RegistroVehiculo = viewModel()
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val hayDatos = s.placa.isNotBlank() || s.marca.isNotBlank() ||
            s.modelo.isNotBlank() || s.anio.isNotBlank() || s.color.isNotBlank()

    fun intentarSalir() {
        if (hayDatos) mostrarDialogo = true else onVolver()
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
            text = { Text("Perderás los datos ingresados. ¿Querés salir de todas formas?") },
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
                title = { Text("Agregar vehículo") },
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
            CampoTexto("Placa", s.placa, vm::onPlaca, placeholder = "Ej: ABC-1234")
            CampoTexto("Marca", s.marca, vm::onMarca, placeholder = "Ej: Toyota")
            CampoTexto("Modelo", s.modelo, vm::onModelo, placeholder = "Ej: Hilux")
            CampoTexto(
                "Año", s.anio, vm::onAnio,
                placeholder = "Ej: 2022",
                tipoTeclado = KeyboardType.Number
            )
            CampoTexto("Color", s.color, vm::onColor, placeholder = "Ej: Blanco")

            CampoSelector(
                etiqueta = "Tipo de vehículo",
                valorSeleccionado = TIPOS_VEHICULO.find { it.first == s.tipoVehiculo }?.second,
                opciones = TIPOS_VEHICULO.map { it.second },
                onSeleccion = { l ->
                    vm.onTipoVehiculo(TIPOS_VEHICULO.first { it.second == l }.first)
                },
                placeholder = "Seleccionar tipo"
            )

            CampoSelector(
                etiqueta = "Tipo de combustible",
                valorSeleccionado = COMBUSTIBLES.find { it.first == s.tipoCombustible }?.second,
                opciones = COMBUSTIBLES.map { it.second },
                onSeleccion = { l ->
                    vm.onTipoCombustible(COMBUSTIBLES.first { it.second == l }.first)
                },
                placeholder = "Seleccionar combustible"
            )

            CampoTexto(
                "Capacidad (opcional)", s.capacidad, vm::onCapacidad,
                placeholder = "Carga en kg o pasajeros",
                tipoTeclado = KeyboardType.Number
            )

            CampoSelector(
                etiqueta = "Conductor asignado (opcional)",
                valorSeleccionado = s.conductores.find { it.id == s.conductorId }?.nombreCompleto,
                opciones = s.conductores.map { it.nombreCompleto },
                onSeleccion = { n ->
                    vm.onConductor(s.conductores.first { it.nombreCompleto == n }.id)
                },
                placeholder = "Seleccionar conductor"
            )

            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Borde)
            Spacer(Modifier.height(4.dp))

            Text(
                text = "Documentos legales",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            CampoFechaSelector(
                etiqueta = "Vencimiento de marchamo",
                valor = s.vencimientoMarchamo,
                onFechaSeleccionada = vm::onVenceMarchamo
            )

            CampoFechaSelector(
                etiqueta = "Vencimiento de revisión técnica",
                valor = s.vencimientoRevisionTecnica,
                onFechaSeleccionada = vm::onVenceRevision
            )

            CampoFechaSelector(
                etiqueta = "Vencimiento de seguro",
                valor = s.vencimientoSeguro,
                onFechaSeleccionada = vm::onVenceSeguro
            )

            Spacer(Modifier.height(8.dp))

            BotonPrimario(
                texto = if (s.guardando) "Guardando..." else "Guardar vehículo",
                onClick = vm::guardar,
                habilitado = s.puedeGuardar
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}