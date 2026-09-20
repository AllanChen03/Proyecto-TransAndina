package com.example.proyecto1ap.vehiculo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vehiculo(
    val id: Long,
    val placa: String,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val color: String,
    @SerialName("tipo_vehiculo") val tipoVehiculo: String,
    @SerialName("tipo_combustible") val tipoCombustible: String,
    val capacidad: Int? = null,
    @SerialName("conductor_id") val conductorId: String? = null,
    val estado: String = "ACTIVO",
    @SerialName("vencimiento_marchamo") val vencimientoMarchamo: String? = null,
    @SerialName("vencimiento_revision_tecnica") val vencimientoRevisionTecnica: String? = null,
    @SerialName("vencimiento_seguro") val vencimientoSeguro: String? = null
)

@Serializable
data class VehiculoNuevo(
    val placa: String,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val color: String,
    @SerialName("tipo_vehiculo") val tipoVehiculo: String,
    @SerialName("tipo_combustible") val tipoCombustible: String,
    val capacidad: Int? = null,
    @SerialName("conductor_id") val conductorId: String? = null,
    @SerialName("vencimiento_marchamo") val vencimientoMarchamo: String? = null,
    @SerialName("vencimiento_revision_tecnica") val vencimientoRevisionTecnica: String? = null,
    @SerialName("vencimiento_seguro") val vencimientoSeguro: String? = null

)
@Serializable
data class VehiculoEditable(
    val placa: String,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val color: String,
    @SerialName("tipo_vehiculo") val tipoVehiculo: String,
    @SerialName("tipo_combustible") val tipoCombustible: String,
    val capacidad: Int? = null,
    @SerialName("conductor_id") val conductorId: String? = null,
    val estado: String,
    @SerialName("vencimiento_marchamo") val vencimientoMarchamo: String? = null,
    @SerialName("vencimiento_revision_tecnica") val vencimientoRevisionTecnica: String? = null,
    @SerialName("vencimiento_seguro") val vencimientoSeguro: String? = null
)
@Serializable
data class VehiculoListado(
    val id: Long,
    val placa: String,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val color: String,
    val estado: String,
    @SerialName("tipo_vehiculo") val tipoVehiculo: String,
    @SerialName("tipo_combustible") val tipoCombustible: String,
    val capacidad: Int? = null,
    @SerialName("conductor_id") val conductorId: String? = null,
    @SerialName("conductor_nombre") val conductorNombre: String? = null,
    @SerialName("conductor_licencia") val conductorLicencia: String? = null,

    @SerialName("kilometraje_actual") val kilometrajeActual: Int? = null,
    @SerialName("fecha_ultimo_km") val fechaUltimoKm: String? = null,

    @SerialName("fecha_ultimo_mantenimiento") val fechaUltimoMantenimiento: String? = null,
    @SerialName("categoria_ultimo_mantenimiento") val categoriaUltimoMantenimiento: String? = null,
    @SerialName("km_ultimo_mantenimiento") val kmUltimoMantenimiento: Int? = null,
    @SerialName("total_mantenimientos") val totalMantenimientos: Int = 0,
    @SerialName("costo_total_mantenimientos") val costoTotalMantenimientos: Double = 0.0,

    @SerialName("vencimiento_marchamo") val vencimientoMarchamo: String? = null,
    @SerialName("vencimiento_revision_tecnica") val vencimientoRevisionTecnica: String? = null,
    @SerialName("vencimiento_seguro") val vencimientoSeguro: String? = null,
    @SerialName("proximo_vencimiento") val proximoVencimiento: String? = null
)