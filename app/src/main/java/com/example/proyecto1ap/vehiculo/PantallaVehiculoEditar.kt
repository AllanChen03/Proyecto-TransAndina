package com.example.proyecto1ap.vehiculo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoSelector
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVehiculoEditar(
    vehiculoId: Long,
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: EditarVehiculo = viewModel(factory = EditarVehiculoFactory(vehiculoId))
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Editar vehículo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Text("←") }
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
            ) {
                CircularProgressIndicator()
            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Estado del vehículo")
                ChipEstado(
                    texto = s.estado,
                    estado = if (s.estado == "ACTIVO") EstadoVisual.OK else EstadoVisual.CRITICO
                )
            }

            CampoTexto("Placa", s.placa, vm::onPlaca)
            CampoTexto("Marca", s.marca, vm::onMarca)
            CampoTexto("Modelo", s.modelo, vm::onModelo)
            CampoTexto("Año", s.anio, vm::onAnio, tipoTeclado = KeyboardType.Number)
            CampoTexto("Color", s.color, vm::onColor)

            CampoSelector(
                etiqueta = "Tipo de vehículo",
                valorSeleccionado = TIPOS_VEHICULO.find { it.first == s.tipoVehiculo }?.second,
                opciones = TIPOS_VEHICULO.map { it.second },
                onSeleccion = { etiqueta ->
                    vm.onTipoVehiculo(TIPOS_VEHICULO.first { it.second == etiqueta }.first)
                },
                placeholder = "Seleccionar tipo"
            )

            CampoSelector(
                etiqueta = "Tipo de combustible",
                valorSeleccionado = COMBUSTIBLES.find { it.first == s.tipoCombustible }?.second,
                opciones = COMBUSTIBLES.map { it.second },
                onSeleccion = { etiqueta ->
                    vm.onTipoCombustible(COMBUSTIBLES.first { it.second == etiqueta }.first)
                },
                placeholder = "Seleccionar combustible"
            )

            CampoTexto(
                etiqueta = "Capacidad",
                valor = s.capacidad,
                onValorChange = vm::onCapacidad,
                placeholder = "Carga en kg o pasajeros",
                tipoTeclado = KeyboardType.Number
            )

            CampoSelector(
                etiqueta = "Conductor asignado",
                valorSeleccionado = s.conductores.find { it.id == s.conductorId }?.nombreCompleto,
                opciones = s.conductores.map { it.nombreCompleto },
                onSeleccion = { nombre ->
                    vm.onConductor(s.conductores.first { it.nombreCompleto == nombre }.id)
                },
                placeholder = "Sin conductor asignado"
            )

            Spacer(Modifier.height(8.dp))

            BotonPrimario(
                texto = if (s.guardando) "Guardando..." else "Guardar cambios",
                onClick = vm::guardar,
                habilitado = s.puedeGuardar
            )

            CampoSelector(
                etiqueta = "Estado",
                valorSeleccionado = ESTADOS.find { it.first == s.estado }?.second,
                opciones = ESTADOS.map { it.second },
                onSeleccion = { etiqueta ->
                    vm.onEstado(ESTADOS.first { it.second == etiqueta }.first)
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}