package com.example.proyecto1ap.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.proyecto1ap.ui.theme.RojoTexto
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.FilaDato
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.AmarilloCampana
import com.example.proyecto1ap.ui.theme.AzulClaro
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.TextoPrincipal

data class OpcionMenu(
    val titulo: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHomeRol(
    usuario: Usuario?,
    opciones: List<OpcionMenu>,
    onAlertas: () -> Unit,
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nombre = usuario?.nombreCompleto ?: "Usuario"

    val iniciales = nombre
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    val etiquetaRol = when (usuario?.rol?.trim()?.uppercase()) {
        "CONDUCTOR" -> "Conductor"
        "MECANICO" -> "Mecánico"
        "ENCARGADO" -> "Encargado de flota"
        else -> "Sin rol"
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Menú principal",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = cerrarSesion) {
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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

            ChipEstado(
                texto = etiquetaRol,
                estado = EstadoVisual.NEUTRO
            )

            Spacer(Modifier.height(24.dp))

            TarjetaSeccion(titulo = "Información personal") {
                FilaDato("Cédula", usuario?.cedula ?: "-")
                FilaDato("Teléfono", usuario?.telefono ?: "-")
                FilaDato("Correo", usuario?.correo ?: "-")
                if (usuario?.numeroLicencia != null) {
                    FilaDato("Licencia", usuario.numeroLicencia)
                }
            }

            Spacer(Modifier.height(20.dp))

            opciones.forEach { opcion ->
                OutlinedButton(
                    onClick = opcion.onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = opcion.titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AzulPrimario
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun PantallaHomeEncargado(
    usuario: Usuario?,
    onGestionFlotilla: () -> Unit = {},
    onGestionUsuarios: () -> Unit = {},
    onAlertas: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    cerrarSesion: () -> Unit
) {
    PantallaHomeRol(
        usuario = usuario,
        opciones = listOf(
            OpcionMenu("Gestión de flotilla", onGestionFlotilla),
            OpcionMenu("Gestión de usuarios", onGestionUsuarios),
            OpcionMenu("Editar perfil", onEditarPerfil)
        ),
        onAlertas = onAlertas,
        cerrarSesion = cerrarSesion
    )
}

@Composable
fun PantallaHomeConductor(
    usuario: Usuario?,
    onRegistrarKilometraje: () -> Unit = {},
    onHistorialKilometraje: () -> Unit = {},
    onVerVehiculo: () -> Unit = {},
    onAlertas: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    cerrarSesion: () -> Unit
) {
    PantallaHomeRol(
        usuario = usuario,
        opciones = listOf(
            OpcionMenu("Registrar kilometraje", onRegistrarKilometraje),
            OpcionMenu("Historial de kilometraje", onHistorialKilometraje),
            OpcionMenu("Ver vehículo asignado", onVerVehiculo),
            OpcionMenu("Editar perfil", onEditarPerfil)
        ),
        onAlertas = onAlertas,
        cerrarSesion = cerrarSesion
    )
}

@Composable
fun PantallaHomeMecanico(
    usuario: Usuario?,
    onRegistrarMantenimiento: () -> Unit = {},
    onHistorialMantenimientos: () -> Unit = {},
    onAlertas: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    cerrarSesion: () -> Unit
) {
    PantallaHomeRol(
        usuario = usuario,
        opciones = listOf(
            OpcionMenu("Registrar mantenimiento", onRegistrarMantenimiento),
            OpcionMenu("Historial de mantenimientos", onHistorialMantenimientos),
            OpcionMenu("Editar perfil", onEditarPerfil)
        ),
        onAlertas = onAlertas,
        cerrarSesion = cerrarSesion
    )
}
