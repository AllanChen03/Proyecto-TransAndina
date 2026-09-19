package com.example.proyecto1ap.vehiculo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoSelector
import com.example.proyecto1ap.ui.componentes.CampoTexto

@Composable
fun PantallaVehiculoRegistro(
    modifier: Modifier = Modifier,
    vm: RegistroVehiculo = viewModel()
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
                onSeleccion = { label ->
                    vm.onTipoVehiculo(TIPOS_VEHICULO.first { it.second == label }.first)
                },
                placeholder = "Seleccionar tipo"
            )

            CampoSelector(
                etiqueta = "Tipo de combustible",
                valorSeleccionado = COMBUSTIBLES.find { it.first == s.tipoCombustible }?.second,
                opciones = COMBUSTIBLES.map { it.second },
                onSeleccion = { label ->
                    vm.onTipoCombustible(COMBUSTIBLES.first { it.second == label }.first)
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
                onSeleccion = { nombre ->
                    vm.onConductor(s.conductores.first { it.nombreCompleto == nombre }.id)
                },
                placeholder = "Seleccionar conductor"
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