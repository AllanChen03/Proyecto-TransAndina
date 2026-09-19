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
    val estado: String = "ACTIVO"
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
    @SerialName("conductor_id") val conductorId: String? = null
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
    val estado: String
)