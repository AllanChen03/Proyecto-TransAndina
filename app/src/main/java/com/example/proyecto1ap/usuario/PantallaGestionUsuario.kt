package com.example.proyecto1ap.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionUsuarios(
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: GestionUsuarios = viewModel()
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    s.usuarioParaAsignar?.let { usuario ->
        DialogoAsignarVehiculo(
            usuario = usuario,
            vehiculos = s.vehiculos,
            onAsignar = { vehiculoId -> vm.asignarVehiculo(usuario.id, vehiculoId) },
            onCerrar = vm::cerrarAsignacion
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gestión de usuarios", fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold)
                        if (!s.cargando) {
                            Text("${s.usuariosFiltrados.size} usuarios",
                                fontSize = 13.sp, color = TextoSecundario)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    null to "Todos",
                    "CONDUCTOR" to "Conductores",
                    "MECANICO" to "Mecánicos",
                    "ENCARGADO" to "Encargados"
                ).forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = s.filtroRol == valor,
                        onClick = { vm.onFiltro(valor) },
                        label = { Text(etiqueta, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrimario,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (s.cargando) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.usuariosFiltrados, key = { it.id }) { usuario ->
                        TarjetaUsuario(
                            usuario = usuario,
                            onCambiarEstado = { nuevo -> vm.cambiarEstado(usuario, nuevo) },
                            onAsignarVehiculo = { vm.abrirAsignacion(usuario) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: UsuarioListado,
    onCambiarEstado: (String) -> Unit,
    onAsignarVehiculo: () -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf<String?>(null) }

    mostrarConfirmacion?.let { nuevoEstado ->
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = null },
            title = { Text("Cambiar estado") },
            text = {
                Text("¿Marcar la cuenta de ${usuario.nombreCompleto} como ${nuevoEstado.lowercase()}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    onCambiarEstado(nuevoEstado)
                    mostrarConfirmacion = null
                }) {
                    Text("Confirmar", color = if (nuevoEstado == "ACTIVO") AzulPrimario else RojoTexto)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = null }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(usuario.nombreCompleto, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = TextoPrincipal)
                    Spacer(Modifier.height(2.dp))
                    Text(usuario.correo, fontSize = 13.sp, color = TextoSecundario)
                    Text("Cédula: ${usuario.cedula}", fontSize = 13.sp, color = TextoSecundario)
                }

                ChipEstado(
                    texto = when (usuario.estado) {
                        "ACTIVO" -> "Activo"
                        "SUSPENDIDO" -> "Suspendido"
                        else -> "Inactivo"
                    },
                    estado = when (usuario.estado) {
                        "ACTIVO" -> EstadoVisual.OK
                        "SUSPENDIDO" -> EstadoVisual.ADVERTENCIA
                        else -> EstadoVisual.CRITICO
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ChipEstado(
                    texto = when (usuario.rol) {
                        "CONDUCTOR" -> "Conductor"
                        "MECANICO" -> "Mecánico"
                        else -> "Encargado"
                    },
                    estado = EstadoVisual.NEUTRO
                )
            }

            if (usuario.rol == "CONDUCTOR") {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Borde)
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Vehículo asignado", fontSize = 12.sp, color = TextoSecundario)
                        Text(
                            usuario.vehiculoPlaca ?: "Ninguno",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    TextButton(onClick = onAsignarVehiculo) {
                        Text("Cambiar", color = AzulPrimario, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Borde)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (usuario.estado != "ACTIVO") {
                    TextButton(onClick = { mostrarConfirmacion = "ACTIVO" }) {
                        Text("Activar", fontSize = 13.sp, color = AzulPrimario)
                    }
                }
                if (usuario.estado != "SUSPENDIDO") {
                    TextButton(onClick = { mostrarConfirmacion = "SUSPENDIDO" }) {
                        Text("Suspender", fontSize = 13.sp, color = TextoSecundario)
                    }
                }
                if (usuario.estado != "INACTIVO") {
                    TextButton(onClick = { mostrarConfirmacion = "INACTIVO" }) {
                        Text("Desactivar", fontSize = 13.sp, color = RojoTexto)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoAsignarVehiculo(
    usuario: UsuarioListado,
    vehiculos: List<com.example.proyecto1ap.vehiculo.Vehiculo>,
    onAsignar: (Long?) -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Asignar vehículo") },
        text = {
            Column {
                Text(
                    "Conductor: ${usuario.nombreCompleto}",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )

                if (usuario.vehiculoPlaca != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Actualmente tiene: ${usuario.vehiculoPlaca}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

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
                                modifier = Modifier.fillMaxWidth()
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
                        Text("Quitar vehículo actual", color = RojoTexto)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}