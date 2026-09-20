package com.example.proyecto1ap.kilometraje

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistroKilometraje(
    val id: Long,
    @SerialName("vehiculo_id") val vehiculoId: Long,
    @SerialName("conductor_id") val conductorId: String,
    val kilometraje: Int,
    val fecha: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class RegistroKilometrajeNuevo(
    @SerialName("vehiculo_id") val vehiculoId: Long,
    @SerialName("conductor_id") val conductorId: String,
    val kilometraje: Int,
    val fecha: String
)