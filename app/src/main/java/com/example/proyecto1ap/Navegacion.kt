package com.example.proyecto1ap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyecto1ap.alertas.PantallaCentroAlertas
import com.example.proyecto1ap.kilometraje.PantallaHistorialKilometraje
import com.example.proyecto1ap.kilometraje.PantallaRegistrarKilometraje
import com.example.proyecto1ap.mantenimiento.PantallaDetalleMantenimiento
import com.example.proyecto1ap.mantenimiento.PantallaEditarMantenimiento
import com.example.proyecto1ap.mantenimiento.PantallaHistorialMantenimientos
import com.example.proyecto1ap.mantenimiento.PantallaRegistrarMantenimiento
import com.example.proyecto1ap.reportes.PantallaReportes
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
import com.example.proyecto1ap.vehiculo.PantallaDetalleVehiculo
import com.example.proyecto1ap.vehiculo.PantallaFlotilla
import com.example.proyecto1ap.vehiculo.PantallaVehiculoAsignado
import com.example.proyecto1ap.vehiculo.PantallaVehiculoEditar
import com.example.proyecto1ap.vehiculo.PantallaVehiculoRegistro

private fun pantallaHomePorRol(rol: String): String {
    return when (rol.trim().uppercase()) {
        "CONDUCTOR" -> "homeConductor"
        "MECANICO" -> "homeMecanico"
        "ENCARGADO" -> "homeEncargado"
        else -> "login"
    }
}

@Composable
fun AppPrincipal(
    modifier: Modifier = Modifier,
    pantallaInicial: String = "login"
) {
    var pantallaActual by remember { mutableStateOf(pantallaInicial) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    // Selección actual
    var vehiculoSeleccionado by remember { mutableStateOf(0L) }
    var usuarioSeleccionado by remember { mutableStateOf("") }
    var mantenimientoSeleccionado by remember { mutableStateOf(0L) }

    // Parámetros del historial de kilometraje
    var historialVehiculoId by remember { mutableStateOf<Long?>(null) }
    var historialVolver by remember { mutableStateOf("homeConductor") }

    // Parámetros del historial de mantenimientos
    var mantenimientoVehiculoId by remember { mutableStateOf<Long?>(null) }
    var mantenimientoSoloMios by remember { mutableStateOf(true) }
    var historialMantVolver by remember { mutableStateOf("homeMecanico") }
    var detalleMantVolver by remember { mutableStateOf("historialMantenimientos") }

    // Otros retornos
    var registroMantVolver by remember { mutableStateOf("homeMecanico") }
    var alertasVolver by remember { mutableStateOf("homeConductor") }

    when (pantallaActual) {

        // ============================================
        // AUTENTICACIÓN
        // ============================================

        "login" -> PantallaInicial(
            modifier = modifier,
            irARegistro = { pantallaActual = "registro" },
            irARecuperarCorreo = { pantallaActual = "recuperarCorreo" },
            irAHomePorRol = { usuario ->
                usuarioActual = usuario
                pantallaActual = pantallaHomePorRol(usuario.rol)
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

        // ============================================
        // HOME POR ROL
        // ============================================

        "homeConductor" -> PantallaHomeConductor(
            usuario = usuarioActual,
            onRegistrarKilometraje = { pantallaActual = "registrarKilometraje" },
            onHistorialKilometraje = {
                historialVehiculoId = null
                historialVolver = "homeConductor"
                pantallaActual = "historialKilometraje"
            },
            onRegistrarMantenimiento = {
                registroMantVolver = "homeConductor"
                pantallaActual = "registrarMantenimiento"
            },
            onHistorialMantenimientos = {
                mantenimientoVehiculoId = null
                mantenimientoSoloMios = true
                historialMantVolver = "homeConductor"
                pantallaActual = "historialMantenimientos"
            },
            onVerVehiculo = { pantallaActual = "vehiculoAsignado" },
            onAlertas = {
                alertasVolver = "homeConductor"
                pantallaActual = "centroAlertas"
            },
            onEditarPerfil = { pantallaActual = "editarPerfil" },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "homeMecanico" -> PantallaHomeMecanico(
            usuario = usuarioActual,
            onRegistrarMantenimiento = {
                registroMantVolver = "homeMecanico"
                pantallaActual = "registrarMantenimiento"
            },
            onHistorialMantenimientos = {
                mantenimientoVehiculoId = null
                mantenimientoSoloMios = true
                historialMantVolver = "homeMecanico"
                pantallaActual = "historialMantenimientos"
            },
            onMantenimiento = { id ->
                mantenimientoSeleccionado = id
                detalleMantVolver = "homeMecanico"
                pantallaActual = "detalleMantenimiento"
            },
            onAlertas = {
                alertasVolver = "homeMecanico"
                pantallaActual = "centroAlertas"
            },
            onEditarPerfil = { pantallaActual = "editarPerfil" },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            },
            modifier = modifier
        )

        "homeEncargado" -> PantallaHomeEncargado(
            usuario = usuarioActual,
            onGestionFlotilla = { pantallaActual = "flotilla" },
            onGestionUsuarios = { pantallaActual = "gestionUsuarios" },
            onHistorialMantenimientos = {
                mantenimientoVehiculoId = null
                mantenimientoSoloMios = false
                historialMantVolver = "homeEncargado"
                pantallaActual = "historialMantenimientos"
            },
            onAlertas = {
                alertasVolver = "homeEncargado"
                pantallaActual = "centroAlertas"
            },
            onReportes = { pantallaActual = "reportes" },
            onEditarPerfil = { pantallaActual = "editarPerfil" },
            cerrarSesion = {
                usuarioActual = null
                pantallaActual = "login"
            }
        )

        "editarPerfil" -> usuarioActual?.let { usuario ->
            PantallaEditarPerfil(
                usuario = usuario,
                onPerfilActualizado = { actualizado ->
                    usuarioActual = actualizado
                    pantallaActual = pantallaHomePorRol(actualizado.rol)
                },
                onVolver = { pantallaActual = pantallaHomePorRol(usuario.rol) },
                modifier = modifier
            )
        }

        // ============================================
        // GESTIÓN DE USUARIOS
        // ============================================

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

        // ============================================
        // GESTIÓN DE FLOTILLA
        // ============================================

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
            onHistorialKilometraje = { id ->
                historialVehiculoId = id
                historialVolver = "detalleVehiculo"
                pantallaActual = "historialKilometraje"
            },
            onHistorialMantenimientos = {
                mantenimientoVehiculoId = vehiculoSeleccionado
                mantenimientoSoloMios = false
                historialMantVolver = "detalleVehiculo"
                pantallaActual = "historialMantenimientos"
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
            onVolver = { pantallaActual = "detalleVehiculo" },
            onGuardado = { pantallaActual = "detalleVehiculo" },
            modifier = modifier
        )

        "vehiculoAsignado" -> usuarioActual?.let { usuario ->
            PantallaVehiculoAsignado(
                conductorId = usuario.id,
                onVolver = { pantallaActual = "homeConductor" },
                onHistorialKilometraje = { id ->
                    historialVehiculoId = id
                    historialVolver = "vehiculoAsignado"
                    pantallaActual = "historialKilometraje"
                },
                onHistorialMantenimientos = { id ->
                    mantenimientoVehiculoId = id
                    mantenimientoSoloMios = false
                    historialMantVolver = "vehiculoAsignado"
                    pantallaActual = "historialMantenimientos"
                },
                modifier = modifier
            )
        }

        // ============================================
        // KILOMETRAJE
        // ============================================

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

        // ============================================
        // MANTENIMIENTOS
        // ============================================

        "registrarMantenimiento" -> PantallaRegistrarMantenimiento(
            conductorId = usuarioActual
                ?.takeIf { it.rol.trim().uppercase() == "CONDUCTOR" }
                ?.id,
            onVolver = { pantallaActual = registroMantVolver },
            onGuardado = { pantallaActual = registroMantVolver },
            modifier = modifier
        )

        "historialMantenimientos" -> PantallaHistorialMantenimientos(
            vehiculoId = mantenimientoVehiculoId,
            soloMios = mantenimientoSoloMios,
            onVolver = { pantallaActual = historialMantVolver },
            onMantenimiento = { id ->
                mantenimientoSeleccionado = id
                detalleMantVolver = "historialMantenimientos"
                pantallaActual = "detalleMantenimiento"
            },
            modifier = modifier
        )

        "detalleMantenimiento" -> PantallaDetalleMantenimiento(
            mantenimientoId = mantenimientoSeleccionado,
            esEncargado = usuarioActual?.rol?.trim()?.uppercase() == "ENCARGADO",
            onVolver = { pantallaActual = detalleMantVolver },
            onEditar = { pantallaActual = "editarMantenimiento" },
            onEliminado = { pantallaActual = detalleMantVolver },
            modifier = modifier
        )

        "editarMantenimiento" -> PantallaEditarMantenimiento(
            mantenimientoId = mantenimientoSeleccionado,
            onVolver = { pantallaActual = "detalleMantenimiento" },
            onGuardado = { pantallaActual = "detalleMantenimiento" },
            modifier = modifier
        )

        // ============================================
        // ALERTAS Y REPORTES
        // ============================================

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
