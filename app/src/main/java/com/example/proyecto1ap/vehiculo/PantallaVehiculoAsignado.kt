package com.example.proyecto1ap.vehiculo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVehiculoAsignado(
    conductorId: String,
    onVolver: () -> Unit,
    onHistorialKilometraje: (Long) -> Unit = {},
    onHistorialMantenimientos: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    vm: VehiculoAsignado = viewModel(
        key = conductorId,
        factory = VehiculoAsignadoFactory(conductorId)
    )
) {
    val s by vm.state.collectAsState()
    val vehiculo = s.vehiculo

    when {
        s.cargando -> Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Vehículo asignado", fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FondoApp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        vehiculo == null -> Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Vehículo asignado", fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FondoApp),
                contentAlignment = Alignment.Center
            ) {
                Text(s.error ?: "No tiene vehículo asignado", color = RojoTexto)
            }
        }

        else -> PantallaDetalleVehiculo(
            vehiculoId = vehiculo.id,
            onVolver = onVolver,
            onHistorialKilometraje = onHistorialKilometraje,
            onHistorialMantenimientos = onHistorialMantenimientos,
            mostrarEditar = false,
            modifier = modifier
        )
    }
}
