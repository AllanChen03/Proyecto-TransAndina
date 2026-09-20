package com.example.proyecto1ap.usuario

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.FilaDato
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.AzulClaro
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.vehiculo.Vehiculo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleUsuario(
    usuarioId: String,
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: DetalleUsuario = viewModel(
        key = usuarioId,
        factory = DetalleUsuarioFactory(usuarioId)
    )
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val u = s.usuario
    var confirmacion by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    val titulo = when (u?.rol) {
        "CONDUCTOR" -> "Detalles del conductor"
        "MECANICO" -> "Detalles del mecánico"
        else -> "Detalles del usuario"
    }

    confirmacion?.let { nuevoEstado ->
        AlertDialog(
            onDismissRequest = { confirmacion = null },
            title = { Text("Cambiar estado") },
            text = { Text("¿Marcar esta cuenta como ${nuevoEstado.lowercase()}?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.cambiarEstado(nuevoEstado)
                    confirmacion = null
                }) {
                    Text(
                        "Confirmar",
                        color = if (nuevoEstado == "ACTIVO") AzulPrimario else RojoTexto
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmacion = null }) { Text("Cancelar") }
            }
        )
    }

    if (s.mostrarAsignacion && u != null) {
        DialogoAsignacion(
            usuario = u,
            vehiculos = s.vehiculosDisponibles,
            onAsignar = vm::asignarVehiculo,
            onCerrar = vm::cerrarAsignacion
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(titulo, fontSize = 18.sp) },
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

            s.error != null || u == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(s.error ?: "Usuario no encontrado", color = RojoTexto) }

            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FondoApp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(AzulClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = u.nombreCompleto.split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .joinToString("") { it.first().uppercase() },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrimario
                    )
                }

                Text(
                    text = u.nombreCompleto,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                ChipEstado(
                    texto = when (u.estado) {
                        "ACTIVO" -> "Activo"
                        "SUSPENDIDO" -> "Suspendido"
                        else -> "Inactivo"
                    },
                    estado = when (u.estado) {
                        "ACTIVO" -> EstadoVisual.OK
                        "SUSPENDIDO" -> EstadoVisual.ADVERTENCIA
                        else -> EstadoVisual.CRITICO
                    }
                )

                Spacer(Modifier.height(8.dp))

                TarjetaSeccion(titulo = "Información personal") {
                    FilaDato("Cédula", u.cedula)
                    FilaDato("Teléfono", u.telefono ?: "—")
                    FilaDato("Correo", u.correo)
                    FilaDato(
                        "Rol",
                        if (u.rol == "CONDUCTOR") "Conductor" else "Mecánico"
                    )
                    if (u.rol == "CONDUCTOR") {
                        FilaDato("Licencia", u.numeroLicencia ?: "Sin registrar")
                    }
                }

                if (u.rol == "CONDUCTOR") {
                    TarjetaSeccion(titulo = "Vehículo asignado") {
                        if (u.vehiculoPlaca != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = u.vehiculoPlaca,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoPrincipal
                                )
                                if (u.estado == "ACTIVO") {
                                    TextButton(onClick = vm::abrirAsignacion) {
                                        Text("Cambiar", color = AzulPrimario, fontSize = 13.sp)
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = Borde)
                            Spacer(Modifier.height(6.dp))

                            FilaDato("Marca", u.vehiculoMarca ?: "—")
                            FilaDato("Modelo", u.vehiculoModelo ?: "—")
                            FilaDato("Año", u.vehiculoAnio?.toString() ?: "—")
                            FilaDato(
                                "Combustible",
                                etiquetaCombustible(u.vehiculoCombustible)
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Sin vehículo asignado",
                                    fontSize = 15.sp,
                                    color = TextoSecundario
                                )
                                if (u.estado == "ACTIVO") {
                                    TextButton(onClick = vm::abrirAsignacion) {
                                        Text("Asignar", color = AzulPrimario, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        if (u.estado != "ACTIVO") {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Activá la cuenta para poder asignar un vehículo",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                }

                TarjetaSeccion(titulo = "Estado de la cuenta") {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (u.estado != "ACTIVO") {
                            TextButton(onClick = { confirmacion = "ACTIVO" }) {
                                Text("Activar", fontSize = 13.sp, color = AzulPrimario)
                            }
                        }
                        if (u.estado != "SUSPENDIDO") {
                            TextButton(onClick = { confirmacion = "SUSPENDIDO" }) {
                                Text("Suspender", fontSize = 13.sp, color = TextoSecundario)
                            }
                        }
                        if (u.estado != "INACTIVO") {
                            TextButton(onClick = { confirmacion = "INACTIVO" }) {
                                Text("Desactivar", fontSize = 13.sp, color = RojoTexto)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DialogoAsignacion(
    usuario: UsuarioListado,
    vehiculos: List<Vehiculo>,
    onAsignar: (Long?) -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Asignar vehículo") },
        text = {
            Column {
                if (usuario.vehiculoPlaca != null) {
                    Text(
                        "Actualmente tiene: ${usuario.vehiculoPlaca}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (vehiculos.isEmpty()) {
                    Text(
                        "No hay vehículos sin conductor disponibles.",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )
                } else {
                    Text(
                        "Vehículos disponibles",
                        fontSize = 12.sp,
                        color = TextoSecundario
                    )
                    vehiculos.forEach { v ->
                        TextButton(
                            onClick = { onAsignar(v.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${v.placa} — ${v.marca} ${v.modelo}",
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (usuario.vehiculoId != null) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = Borde)
                    TextButton(
                        onClick = { onAsignar(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quitar vehículo actual", color = RojoTexto, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}

private fun etiquetaCombustible(valor: String?): String = when (valor) {
    "SUPER" -> "Súper"
    "REGULAR" -> "Regular"
    "DIESEL" -> "Diésel"
    "ELECTRICO" -> "Eléctrico"
    "HIBRIDO" -> "Híbrido"
    else -> "—"
}