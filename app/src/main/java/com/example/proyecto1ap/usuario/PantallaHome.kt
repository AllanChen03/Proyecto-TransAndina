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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.proyecto1ap.mantenimiento.InicioMecanico
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.FilaDato
import com.example.proyecto1ap.ui.componentes.MiniTarjetaEstadistica
import com.example.proyecto1ap.ui.componentes.TarjetaRegistroMantenimiento
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.AmarilloCampana
import com.example.proyecto1ap.ui.theme.AzulClaro
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoBorde
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

data class OpcionMenu(
    val titulo: String,
    val onClick: () -> Unit
)

private fun formatoCalificacion(calificacion: Double?): String {
    return calificacion?.let { "%.1f / 5".format(it) } ?: "Sin calificación"
}

/** Botón de opción del menú, común a todos los homes. */
@Composable
private fun BotonOpcion(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = AzulPrimario
        )
    }
}

/** Avatar, saludo, chip de rol e información personal. */
@Composable
private fun CabeceraPerfil(usuario: Usuario?, etiquetaRol: String) {
    val nombre = usuario?.nombreCompleto ?: "Usuario"

    val iniciales = nombre
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    val rolUsuario = usuario?.rol?.trim()?.uppercase()
    val puedeSerCalificado = rolUsuario == "CONDUCTOR" || rolUsuario == "MECANICO"

    Spacer(Modifier.height(24.dp))

    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(AzulClaro),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales.ifBlank { "?" },
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = AzulPrimario
        )
    }

    Spacer(Modifier.height(16.dp))

    Text(
        text = "Hola, $nombre",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextoPrincipal
    )

    Spacer(Modifier.height(8.dp))

    ChipEstado(texto = etiquetaRol, estado = EstadoVisual.NEUTRO)

    Spacer(Modifier.height(24.dp))

    TarjetaSeccion(titulo = "Información personal") {
        FilaDato("Cédula", usuario?.cedula ?: "-")
        FilaDato("Teléfono", usuario?.telefono ?: "-")
        FilaDato("Correo", usuario?.correo ?: "-")
        if (puedeSerCalificado) {
            FilaDato("Calificación", formatoCalificacion(usuario?.calificacion))
        }
        if (usuario?.numeroLicencia != null) {
            FilaDato("Licencia", usuario.numeroLicencia)
        }
    }
}

@Composable
private fun DialogoCerrarSesion(onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cerrar sesión") },
        text = { Text("¿Querés salir de tu cuenta?") },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Sí, salir", color = RojoTexto)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

/** Home genérico para conductor y encargado. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomeRol(
    usuario: Usuario?,
    opciones: List<OpcionMenu>,
    onAlertas: () -> Unit,
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogoCerrar by remember { mutableStateOf(false) }

    val etiquetaRol = when (usuario?.rol?.trim()?.uppercase()) {
        "CONDUCTOR" -> "Conductor"
        "MECANICO" -> "Mecánico"
        "ENCARGADO" -> "Encargado de flota"
        else -> "Sin rol"
    }

    if (mostrarDialogoCerrar) {
        DialogoCerrarSesion(
            onConfirmar = {
                mostrarDialogoCerrar = false
                cerrarSesion()
            },
            onCancelar = { mostrarDialogoCerrar = false }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Menú principal", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { mostrarDialogoCerrar = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = RojoTexto
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAlertas) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Alertas",
                            tint = AmarilloCampana
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CabeceraPerfil(usuario, etiquetaRol)

            Spacer(Modifier.height(20.dp))

            opciones.forEach { opcion ->
                BotonOpcion(opcion.titulo, opcion.onClick)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ============================================
// HOME ENCARGADO
// ============================================

@Composable
fun PantallaHomeEncargado(
    usuario: Usuario?,
    onGestionFlotilla: () -> Unit = {},
    onGestionUsuarios: () -> Unit = {},
    onHistorialMantenimientos: () -> Unit = {},
    onReportes: () -> Unit = {},
    onAlertas: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    PantallaHomeRol(
        usuario = usuario,
        opciones = listOf(
            OpcionMenu("Gestión de flotilla", onGestionFlotilla),
            OpcionMenu("Gestión de usuarios", onGestionUsuarios),
            OpcionMenu("Historial de mantenimientos", onHistorialMantenimientos),
            OpcionMenu("Reportes de mantenimiento", onReportes),
            OpcionMenu("Editar perfil", onEditarPerfil)
        ),
        onAlertas = onAlertas,
        cerrarSesion = cerrarSesion,
        modifier = modifier
    )
}

// ============================================
// HOME CONDUCTOR
// ============================================

@Composable
fun PantallaHomeConductor(
    usuario: Usuario?,
    onRegistrarKilometraje: () -> Unit = {},
    onHistorialKilometraje: () -> Unit = {},
    onRegistrarMantenimiento: () -> Unit = {},
    onHistorialMantenimientos: () -> Unit = {},
    onVerVehiculo: () -> Unit = {},
    onAlertas: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    PantallaHomeRol(
        usuario = usuario,
        opciones = listOf(
            OpcionMenu("Registrar kilometraje", onRegistrarKilometraje),
            OpcionMenu("Historial de kilometraje", onHistorialKilometraje),
            OpcionMenu("Registrar mantenimiento", onRegistrarMantenimiento),
            OpcionMenu("Historial de mantenimientos", onHistorialMantenimientos),
            OpcionMenu("Ver vehículo asignado", onVerVehiculo),
            OpcionMenu("Historial de mantenimientos", onHistorialMantenimientos),
            OpcionMenu("Editar perfil", onEditarPerfil)
        ),
        onAlertas = onAlertas,
        cerrarSesion = cerrarSesion,
        modifier = modifier
    )
}

// ============================================
// HOME MECÁNICO
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomeMecanico(
    usuario: Usuario?,
    onRegistrarMantenimiento: () -> Unit = {},
    onHistorialMantenimientos: () -> Unit = {},
    onMantenimiento: (Long) -> Unit = {},
    onAlertas: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    vm: InicioMecanico = viewModel()
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var mostrarDialogoCerrar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargar() }

    LaunchedEffect(s.error) {
        s.error?.let { snackbar.showSnackbar(it) }
    }

    if (mostrarDialogoCerrar) {
        DialogoCerrarSesion(
            onConfirmar = {
                mostrarDialogoCerrar = false
                cerrarSesion()
            },
            onCancelar = { mostrarDialogoCerrar = false }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Menú principal", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { mostrarDialogoCerrar = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = RojoTexto
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAlertas) {
                        BadgedBox(
                            badge = {
                                if (s.alertasActivas > 0) {
                                    Badge(
                                        containerColor = RojoBorde,
                                        contentColor = Color.White
                                    ) {
                                        Text(s.alertasActivas.toString(), fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Alertas",
                                tint = AmarilloCampana
                            )
                        }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CabeceraPerfil(usuario, "Mecánico")

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniTarjetaEstadistica(
                    titulo = "Mantenimientos",
                    valor = if (s.cargando) "..." else s.totalMantenimientos.toString(),
                    icono = Icons.Filled.Build,
                    modifier = Modifier.weight(1f)
                )
                MiniTarjetaEstadistica(
                    titulo = "Calificación",
                    valor = if (s.cargando) "..."
                    else "%.1f".format(s.calificacion).replace(",", "."),
                    icono = Icons.Filled.Star,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------- Últimos mantenimientos ----------

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Últimos mantenimientos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                when {
                    s.cargando -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando...", fontSize = 14.sp, color = TextoSecundario)
                    }

                    s.ultimos.isEmpty() -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Todavía no registraste mantenimientos",
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )
                    }

                    else -> s.ultimos.forEach { registro ->
                        TarjetaRegistroMantenimiento(
                            registro = registro,
                            onClick = { onMantenimiento(registro.id) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            BotonOpcion("Registrar mantenimiento", onRegistrarMantenimiento)
            Spacer(Modifier.height(10.dp))

            BotonOpcion("Historial de mantenimientos", onHistorialMantenimientos)
            Spacer(Modifier.height(10.dp))

            BotonOpcion("Editar perfil", onEditarPerfil)

            Spacer(Modifier.height(32.dp))
        }
    }
}