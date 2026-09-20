package com.example.proyecto1ap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyecto1ap.usuario.PantallaCambiarContrasena
import com.example.proyecto1ap.usuario.PantallaGestionUsuarios
import com.example.proyecto1ap.usuario.PantallaHomeConductor
import com.example.proyecto1ap.usuario.PantallaHomeEncargado
import com.example.proyecto1ap.usuario.PantallaHomeMecanico
import com.example.proyecto1ap.usuario.PantallaInicial
import com.example.proyecto1ap.usuario.PantallaRecuperarCorreo
import com.example.proyecto1ap.usuario.PantallaRegistro
import com.example.proyecto1ap.usuario.Usuario
import com.example.proyecto1ap.vehiculo.PantallaFlotilla
import com.example.proyecto1ap.vehiculo.PantallaVehiculoEditar
import com.example.proyecto1ap.vehiculo.PantallaVehiculoRegistro
import com.example.proyecto1ap.kilometraje.PantallaRegistrarKilometraje
import com.example.proyecto1ap.kilometraje.PantallaHistorialKilometraje
import com.example.proyecto1ap.alertas.PantallaCentroAlertas
import com.example.proyecto1ap.reportes.PantallaReportes
@Composable
fun AppPrincipal(
    modifier: Modifier = Modifier,
    pantallaInicial: String = "login"
) {
    var pantallaActual by remember { mutableStateOf(pantallaInicial) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var vehiculoSeleccionado by remember { mutableStateOf(0L) }
    var historialVehiculoId by remember { mutableStateOf<Long?>(null) }
    var historialVolver by remember { mutableStateOf("homeConductor") }
    var alertasVolver by remember { mutableStateOf("homeConductor") }

    when (pantallaActual) {
        "login" -> PantallaInicial(
            modifier = modifier,
            irARegistro = { pantallaActual = "registro" },
            irARecuperarCorreo = { pantallaActual = "recuperarCorreo" },
            irAHomePorRol = { usuario ->
                usuarioActual = usuario
                pantallaActual = when (usuario.rol.trim().uppercase()) {
                    "CONDUCTOR" -> "homeConductor"
                    "MECANICO" -> "homeMecanico"
                    "ENCARGADO" -> "homeEncargado"
                    else -> "login"
                }
            }
        )

        "registro" -> PantallaRegistro(
            modifier = modifier,
            volverALogin = { pantallaActual = "login" },
            registroExitoso = { pantallaActual = "login" }
        )

        "recuperarCorreo" -> PantallaRecuperarCorreo(
            modifier = modifier,
            volverALogin = { pantallaActual = "login" }
        )

        "cambiarContrasena" -> PantallaCambiarContrasena(
            modifier = modifier,
            volverALogin = { pantallaActual = "login" },
            cambioExitoso = { pantallaActual = "login" }
        )

        "homeConductor" -> PantallaHomeConductor(
            usuario = usuarioActual,
            onRegistrarKilometraje = { pantallaActual = "registrarKilometraje" },
            onHistorialKilometraje = {
                historialVehiculoId = null
                historialVolver = "homeConductor"
                pantallaActual = "historialKilometraje"
            },
            onAlertas = { alertasVolver = "homeConductor"; pantallaActual = "centroAlertas" },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }

        )

        "homeMecanico" -> PantallaHomeMecanico(
            usuario = usuarioActual,
            onAlertas = { alertasVolver = "homeMecanico"; pantallaActual = "centroAlertas" },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }

        )

        "homeEncargado" -> PantallaHomeEncargado(
            usuario = usuarioActual,
            onGestionFlotilla = { pantallaActual = "flotilla" },
            onGestionUsuarios = { pantallaActual = "gestionUsuarios" },
            onAlertas = { alertasVolver = "homeEncargado"; pantallaActual = "centroAlertas" },
            onReportes = { pantallaActual = "reportes" },
            cerrarSesion = { usuarioActual = null; pantallaActual = "login" }
        )

        "gestionUsuarios" -> PantallaGestionUsuarios(
            onVolver = { pantallaActual = "homeEncargado" },
            modifier = modifier
        )

        "flotilla" -> PantallaFlotilla(
            onVolver = { pantallaActual = "homeEncargado" },
            onAgregar = { pantallaActual = "registroVehiculo" },
            onVehiculo = { id ->
                vehiculoSeleccionado = id
                pantallaActual = "editarVehiculo"
            },
            modifier = modifier
        )

        "registroVehiculo" -> PantallaVehiculoRegistro(
            onVolver = { pantallaActual = "flotilla" },
            onGuardado = { pantallaActual = "flotilla" },
            modifier = modifier
        )

        "editarVehiculo" -> PantallaVehiculoEditar(
            vehiculoId = vehiculoSeleccionado,
            onVolver = { pantallaActual = "flotilla" },
            onHistorialKm = {
                historialVehiculoId = vehiculoSeleccionado
                historialVolver = "editarVehiculo"
                pantallaActual = "historialKilometraje"
            },
            modifier = modifier

        )

        "registrarKilometraje" -> usuarioActual?.let { u ->
            PantallaRegistrarKilometraje(
                conductorId = u.id,
                onVolver = { pantallaActual = "homeConductor" },
                onGuardado = { pantallaActual = "homeConductor" },
                modifier = modifier
            )
        }

        "historialKilometraje" -> PantallaHistorialKilometraje(
            vehiculoId = historialVehiculoId,
            conductorId = usuarioActual?.id,
            onVolver = { pantallaActual = historialVolver },
            modifier = modifier
        )
        "centroAlertas" -> PantallaCentroAlertas(
            conductorId = usuarioActual
                ?.takeIf { it.rol.trim().uppercase() == "CONDUCTOR" }
                ?.id,
            usuarioId = usuarioActual?.id,
            onVolver = { pantallaActual = alertasVolver },
            modifier = modifier
        )
        "reportes" -> PantallaReportes(
            onVolver = { pantallaActual = "homeEncargado" },
            modifier = modifier
        )
    }
}