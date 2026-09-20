package com.example.proyecto1ap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyecto1ap.usuario.PantallaCambiarContrasena
import com.example.proyecto1ap.usuario.PantallaDetalleUsuario
import com.example.proyecto1ap.usuario.PantallaEditarPerfil
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
import com.example.proyecto1ap.vehiculo.PantallaDetalleVehiculo

@Composable
fun AppPrincipal(
    modifier: Modifier = Modifier,
    pantallaInicial: String = "login"
) {
    var pantallaActual by remember { mutableStateOf(pantallaInicial) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }
    var vehiculoSeleccionado by remember { mutableStateOf(0L) }
    var usuarioSeleccionado by remember { mutableStateOf("") }
    var pantallaAnterior by remember { mutableStateOf("login") }

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
            onEditarPerfil = {
                pantallaAnterior = "homeConductor"
                pantallaActual = "editarPerfil"
            },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "homeMecanico" -> PantallaHomeMecanico(
            usuario = usuarioActual,
            onEditarPerfil = {
                pantallaAnterior = "homeMecanico"
                pantallaActual = "editarPerfil"
            },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "homeEncargado" -> PantallaHomeEncargado(
            usuario = usuarioActual,
            onGestionFlotilla = { pantallaActual = "flotilla" },
            onGestionUsuarios = { pantallaActual = "gestionUsuarios" },
            onEditarPerfil = {
                pantallaAnterior = "homeEncargado"
                pantallaActual = "editarPerfil"
            },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "editarPerfil" -> PantallaEditarPerfil(
            onVolver = { pantallaActual = pantallaAnterior },
            modifier = modifier
        )

        "gestionUsuarios" -> PantallaGestionUsuarios(
            onVolver = { pantallaActual = "homeEncargado" },
            onUsuario = { id ->
                usuarioSeleccionado = id
                pantallaActual = "detalleUsuario"
            },
            modifier = modifier
        )


        "detalleUsuario" -> PantallaDetalleUsuario(
            usuarioId = usuarioSeleccionado,
            onVolver = { pantallaActual = "gestionUsuarios" },
            modifier = modifier
        )


        "flotilla" -> PantallaFlotilla(
            onVolver = { pantallaActual = "homeEncargado" },
            onAgregar = { pantallaActual = "registroVehiculo" },
            onVehiculo = { id ->
                vehiculoSeleccionado = id
                pantallaActual = "detalleVehiculo"
            },
            modifier = modifier
        )

        "detalleVehiculo" -> PantallaDetalleVehiculo(
            vehiculoId = vehiculoSeleccionado,
            onVolver = { pantallaActual = "flotilla" },
            onEditar = { pantallaActual = "editarVehiculo" },
            modifier = modifier
        )

        "editarVehiculo" -> PantallaVehiculoEditar(
            vehiculoId = vehiculoSeleccionado,
            onVolver = { pantallaActual = "detalleVehiculo" },
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
    }
}