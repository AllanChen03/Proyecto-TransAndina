package com.example.proyecto1ap.reportes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehiculoResumen(
    val placa: String,
    val marca: String? = null,
    val modelo: String? = null
)

@Serializable
data class MecanicoResumen(
    @SerialName("nombre_completo") val nombreCompleto: String
)

@Serializable
data class MantenimientoReporte(
    val id: Long,
    @SerialName("tipo_mantenimiento") val tipoMantenimiento: String,
    val descripcion: String,
    @SerialName("fecha_mantenimiento") val fecha: String,
    @SerialName("costo_aproximado") val costo: Double? = null,
    @SerialName("categoria_servicio") val categoria: String = "",
    val vehiculos: VehiculoResumen? = null,
    val usuarios: MecanicoResumen? = null
)

data class FiltrosReporte(
    val vehiculoId: Long? = null,
    val tipo: String? = null,
    val desde: String = "",
    val hasta: String = "",
    val costoMin: String = "",
    val costoMax: String = ""
)