package com.example.proyecto1ap.alertas

import com.example.proyecto1ap.vehiculo.VehiculoListado
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Serializable
data class ConfiguracionMantenimiento(
    @SerialName("intervalo_km") val intervaloKm: Int = 5000,
    @SerialName("umbral_alerta_km") val umbralAlertaKm: Int = 500
)

enum class Severidad { URGENTE, ADVERTENCIA }

enum class CategoriaAlerta { MANTENIMIENTO, DOCUMENTO }

data class AlertaCalculada(
    val categoria: CategoriaAlerta,
    val placa: String,
    val titulo: String,
    val detalle: String,
    val severidad: Severidad
)

private const val DIAS_AVISO_DOCUMENTO = 30

fun calcularAlertas(
    vehiculos: List<VehiculoListado>,
    config: ConfiguracionMantenimiento,
    hoy: LocalDate = LocalDate.now()
): List<AlertaCalculada> {
    val resultado = mutableListOf<AlertaCalculada>()

    for (v in vehiculos.filter { it.estado == "ACTIVO" }) {

        // Mantenimiento preventivo por kilometraje
        val kmActual = v.kilometrajeActual
        if (kmActual != null) {
            val base = v.kmUltimoMantenimiento ?: 0
            val proximo = base + config.intervaloKm
            val restante = proximo - kmActual
            when {
                restante < 0 -> resultado += AlertaCalculada(
                    CategoriaAlerta.MANTENIMIENTO, v.placa,
                    "Mantenimiento atrasado",
                    "Se pasó por ${"%,d".format(-restante)} km (correspondía a los ${"%,d".format(proximo)} km)",
                    Severidad.URGENTE
                )
                restante <= config.umbralAlertaKm -> resultado += AlertaCalculada(
                    CategoriaAlerta.MANTENIMIENTO, v.placa,
                    "Mantenimiento próximo",
                    "Requerido en ${"%,d".format(restante)} km (a los ${"%,d".format(proximo)} km)",
                    Severidad.ADVERTENCIA
                )
            }
        }

        // Documentos legales
        val documentos = listOf(
            "Marchamo" to v.vencimientoMarchamo,
            "Revisión técnica" to v.vencimientoRevisionTecnica,
            "Seguro" to v.vencimientoSeguro
        )
        for ((nombre, texto) in documentos) {
            val fecha = texto?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: continue
            val dias = ChronoUnit.DAYS.between(hoy, fecha)
            when {
                dias < 0 -> resultado += AlertaCalculada(
                    CategoriaAlerta.DOCUMENTO, v.placa, nombre,
                    "Venció el $fecha • Hace ${-dias} días",
                    Severidad.URGENTE
                )
                dias <= DIAS_AVISO_DOCUMENTO -> resultado += AlertaCalculada(
                    CategoriaAlerta.DOCUMENTO, v.placa, nombre,
                    "Vence el $fecha • Faltan $dias días",
                    Severidad.ADVERTENCIA
                )
            }
        }
    }

    return resultado.sortedWith(compareBy({ it.severidad }, { it.placa }))
}
@Serializable
data class NotificacionFila(
    val id: Long,
    @SerialName("vehiculo_id") val vehiculoId: Long,
    val tipo: String,
    val titulo: String,
    val mensaje: String,
    val prioridad: String,
    @SerialName("created_at") val createdAt: String
)