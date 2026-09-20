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
import com.example.proyecto1ap.Vehiculo.PantallaFlotilla
import com.example.proyecto1ap.Vehiculo.PantallaVehiculoEditar
import com.example.proyecto1ap.Vehiculo.PantallaVehiculoRegistro
import com.example.proyecto1ap.kilometraje.PantallaRegistrarKilometraje

@Composable
fun AppPrincipal(
    modifier: Modifier = Modifier,
    pantallaInicial: String = "login"
) {
    var pantallaActual by remember { mutableStateOf(pantallaInicial) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var vehiculoSeleccionado by remember { mutableStateOf(0L) }

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
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "homeMecanico" -> PantallaHomeMecanico(
            usuario = usuarioActual,
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "homeEncargado" -> PantallaHomeEncargado(
            usuario = usuarioActual,
            onGestionFlotilla = { pantallaActual = "flotilla" },
            onGestionUsuarios = { pantallaActual = "gestionUsuarios" },
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
    }
}