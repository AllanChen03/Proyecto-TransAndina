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
@Serializable
data class UsuarioNombre(
    @SerialName("nombre_completo") val nombreCompleto: String
)

@Serializable
data class RegistroConUsuario(
    val id: Long,
    val kilometraje: Int,
    val fecha: String,
    @SerialName("conductor_id") val conductorId: String,
    val usuarios: UsuarioNombre? = null
)