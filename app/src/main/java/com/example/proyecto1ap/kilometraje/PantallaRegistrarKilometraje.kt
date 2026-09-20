package com.example.proyecto1ap.kilometraje

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoFecha
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.MensajeAdvertencia
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarKilometraje(
    conductorId: String,
    onVolver: () -> Unit = {},
    onGuardado: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: RegistrarKilometraje = viewModel(
        key = remember { UUID.randomUUID().toString() },
        factory = RegistrarKilometrajeFactory(conductorId)
    )
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    LaunchedEffect(s.exito) {
        if (s.exito) onGuardado()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Registrar kilometraje") },
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

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
        ) {
            when {
                s.cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                s.error != null -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.error ?: "", color = RojoTexto, fontSize = 14.sp)
                    TextButton(onClick = vm::cargar) { Text("Reintentar") }
                }

                else -> {
                    val vehiculo = s.vehiculo
                    val fechaLegible = remember(s.fecha) {
                        LocalDate.parse(s.fecha).format(
                            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "CR"))
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (vehiculo != null) {
                            TarjetaSeccion(titulo = "Vehículo asignado") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = vehiculo.placa,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoPrincipal
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.anio})",
                                            fontSize = 14.sp,
                                            color = TextoSecundario
                                        )
                                    }
                                    ChipEstado(
                                        texto = if (vehiculo.estado == "ACTIVO") "ACTIVO" else "INACTIVO",
                                        estado = if (vehiculo.estado == "ACTIVO")
                                            EstadoVisual.NEUTRO else EstadoVisual.CRITICO
                                    )
                                }
                            }
                        }

                        CampoTexto(
                            etiqueta = "Kilometraje actual (km)",
                            valor = s.kilometraje,
                            onValorChange = vm::onKilometraje,
                            placeholder = s.ultimoKm?.let { "Último registro: %,d km".format(it) }
                                ?: "Ej: 45000",
                            error = if (s.kmMenorAlUltimo) "menor" else null,
                            tipoTeclado = KeyboardType.Number
                        )

                        if (s.kmMenorAlUltimo) {
                            MensajeAdvertencia(
                                "El kilometraje ingresado no puede ser menor al último " +
                                        "registro (${"%,d".format(s.ultimoKm)} km)"
                            )
                        }

                        CampoFecha(
                            etiqueta = "Fecha de registro",
                            fechaTexto = fechaLegible,
                            onClick = { }
                        )

                        Spacer(Modifier.height(4.dp))

                        BotonPrimario(
                            texto = if (s.guardando) "Guardando..." else "Guardar registro",
                            onClick = vm::guardar,
                            habilitado = s.puedeGuardar
                        )
                    }
                }
            }
        }
    }
}