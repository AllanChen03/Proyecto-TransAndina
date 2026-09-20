package com.example.proyecto1ap.mantenimiento

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val CATEGORIAS_SERVICIO = listOf(
    "CAMBIO_ACEITE" to "Cambio de aceite",
    "FRENOS" to "Frenos",
    "LLANTAS" to "Llantas",
    "REVISION_GENERAL" to "Revisión general",
    "OTRO" to "Otro"
)

val TALLERES = listOf(
    "Juan Carlos Ruiz (Taller Central)",
    "Mecánica Express San José",
    "Taller El Escazú",
    "Servicio Técnico Andina"
)

fun etiquetaCategoria(valor: String?): String? =
    CATEGORIAS_SERVICIO.firstOrNull { it.first == valor }?.second

fun formatearFecha(iso: String): String =
    runCatching {
        LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }.getOrDefault(iso)

fun formatearCosto(monto: Double?): String =
    monto?.let { "₡%,.0f".format(it).replace(",", ".") } ?: "-"