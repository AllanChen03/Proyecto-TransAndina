package com.example.proyecto1ap.vehiculo

import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.theme.AmarilloTexto
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.ui.theme.VerdeTexto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


object ReglasMantenimiento {
    const val KM_ENTRE_SERVICIOS = 5000
    const val KM_AVISO_PREVIO = 500
    const val DIAS_ENTRE_SERVICIOS = 180
    const val DIAS_AVISO_PREVIO = 30
}

fun estadoMantenimiento(v: VehiculoListado): Pair<String, EstadoVisual> {
    if (v.estado == "INACTIVO") {
        return "Fuera de servicio" to EstadoVisual.NEUTRO
    }

    if (v.totalMantenimientos == 0) {
        return "Sin mantenimientos" to EstadoVisual.NEUTRO
    }

    val kmActual = v.kilometrajeActual
    val kmUltimo = v.kmUltimoMantenimiento

    if (kmActual != null && kmUltimo != null) {
        val recorrido = kmActual - kmUltimo
        val restante = ReglasMantenimiento.KM_ENTRE_SERVICIOS - recorrido

        return when {
            restante <= 0 ->
                "Mantenimiento atrasado" to EstadoVisual.CRITICO
            restante <= ReglasMantenimiento.KM_AVISO_PREVIO ->
                "Próximo a mantenimiento ($restante km)" to EstadoVisual.ADVERTENCIA
            else ->
                "Al día ($restante km)" to EstadoVisual.OK
        }
    }

    val fechaUltimo = parsearFecha(v.fechaUltimoMantenimiento)
    if (fechaUltimo != null) {
        val dias = ChronoUnit.DAYS.between(fechaUltimo, LocalDate.now())
        val restantes = ReglasMantenimiento.DIAS_ENTRE_SERVICIOS - dias

        return when {
            restantes <= 0 ->
                "Mantenimiento atrasado" to EstadoVisual.CRITICO
            restantes <= ReglasMantenimiento.DIAS_AVISO_PREVIO ->
                "Próximo a mantenimiento ($restantes días)" to EstadoVisual.ADVERTENCIA
            else ->
                "Al día" to EstadoVisual.OK
        }
    }

    return "Sin datos suficientes" to EstadoVisual.NEUTRO
}

data class VigenciaDocumento(
    val etiqueta: String,
    val estado: EstadoVisual,
    val textoFecha: String
)

private val FORMATO = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es"))

fun parsearFecha(texto: String?): LocalDate? =
    texto?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

fun evaluarVigencia(fechaTexto: String?): VigenciaDocumento {
    val fecha = parsearFecha(fechaTexto)
        ?: return VigenciaDocumento("Sin registrar", EstadoVisual.NEUTRO, "—")

    val hoy = LocalDate.now()
    val dias = ChronoUnit.DAYS.between(hoy, fecha)
    val fechaLegible = fecha.format(FORMATO)

    return when {
        dias < 0 ->
            VigenciaDocumento("Vencido", EstadoVisual.CRITICO, "Venció: $fechaLegible")
        dias <= 30 ->
            VigenciaDocumento("Por vencer", EstadoVisual.ADVERTENCIA, "Vence: $fechaLegible")
        else ->
            VigenciaDocumento("Vigente", EstadoVisual.OK, "Vence: $fechaLegible")
    }
}

fun estadoGeneral(v: VehiculoListado): Pair<String, EstadoVisual> {
    if (v.estado == "INACTIVO") {
        return "Fuera de servicio" to EstadoVisual.NEUTRO
    }

    val fechas = listOfNotNull(
        parsearFecha(v.vencimientoMarchamo),
        parsearFecha(v.vencimientoRevisionTecnica),
        parsearFecha(v.vencimientoSeguro)
    )

    if (fechas.isEmpty()) {
        return "Sin documentos" to EstadoVisual.NEUTRO
    }

    val hoy = LocalDate.now()
    val vencidos = fechas.count { it.isBefore(hoy) }
    if (vencidos > 0) {
        return "Documentos vencidos ($vencidos)" to EstadoVisual.CRITICO
    }

    val dias = ChronoUnit.DAYS.between(hoy, fechas.min())
    if (dias <= 30) {
        return "Documento vence en $dias días" to EstadoVisual.ADVERTENCIA
    }

    return "Documentos al día" to EstadoVisual.OK
}

fun colorDe(estado: EstadoVisual) = when (estado) {
    EstadoVisual.OK -> VerdeTexto
    EstadoVisual.ADVERTENCIA -> AmarilloTexto
    EstadoVisual.CRITICO -> RojoTexto
    EstadoVisual.NEUTRO -> TextoSecundario
}