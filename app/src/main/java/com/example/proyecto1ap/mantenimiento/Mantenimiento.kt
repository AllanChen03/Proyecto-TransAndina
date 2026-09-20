package com.example.proyecto1ap.mantenimiento

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TipoMantenimiento(val etiqueta: String) {
    PREVENTIVO("Preventivo"),
    CORRECTIVO("Correctivo");

    companion object {
        fun desde(valor: String?): TipoMantenimiento =
            entries.firstOrNull { it.name == valor?.trim()?.uppercase() } ?: PREVENTIVO

        fun listado(): List<String> = entries.map { it.etiqueta }
    }
}

@Serializable
data class MantenimientoNuevo(
    @SerialName("vehiculo_id") val vehiculoId: Long,
    @SerialName("tipo_mantenimiento") val tipoMantenimiento: String,
    @SerialName("categoria_servicio") val categoriaServicio: String,
    @SerialName("fecha_mantenimiento") val fechaMantenimiento: String,
    val kilometraje: Int? = null,
    val descripcion: String,
    @SerialName("costo_aproximado") val costoAproximado: Double? = null,
    val taller: String? = null,
    @SerialName("mecanico_id") val mecanicoId: String
)

@Serializable
data class MantenimientoFila(
    val id: Long,
    @SerialName("vehiculo_id") val vehiculoId: Long,
    @SerialName("tipo_mantenimiento") val tipoMantenimiento: String,
    @SerialName("categoria_servicio") val categoriaServicio: String? = null,
    @SerialName("fecha_mantenimiento") val fechaMantenimiento: String,
    val descripcion: String,
    @SerialName("costo_aproximado") val costoAproximado: Double? = null,
    val taller: String? = null,
    val kilometraje: Int? = null,
    @SerialName("fecha_proximo_mantenimiento") val fechaProximoMantenimiento: String? = null,
    @SerialName("mecanico_id") val mecanicoId: String? = null
)

data class MantenimientoListado(
    val id: Long,
    val vehiculoPlaca: String,
    val vehiculoDescripcion: String,
    val tipo: TipoMantenimiento,
    val fecha: String,
    val titulo: String,
    val categoriaServicio: String? = null,
    val mecanico: String? = null,
    val costo: Double? = null
)

data class ResumenVehiculo(
    val id: Long,
    val placa: String,
    val marcaModelo: String,
    val combustible: String = ""
) {
    val descripcion: String
        get() = listOf(marcaModelo, combustible).filter { it.isNotBlank() }.joinToString(" · ")
}