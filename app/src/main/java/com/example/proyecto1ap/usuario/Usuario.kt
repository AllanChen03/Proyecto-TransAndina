package com.example.proyecto1ap.usuario

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**Clase usuario-tabla usuario**/
@Serializable
data class Usuario(
    val id: String,
    @SerialName("nombre_completo") val nombreCompleto: String,
    val cedula: String,
    val correo: String,
    val telefono: String? = null,
    @SerialName("numero_licencia") val numeroLicencia: String? = null,
    val rol: String,
    val estado: String = "ACTIVO",
    val calificacion: Double? = null
)

/**Clase UsuarioLista-tabla usuario_listado**/
@Serializable
data class UsuarioListado(
    val id: String,
    @SerialName("nombre_completo") val nombreCompleto: String,
    val cedula: String,
    val correo: String,
    val telefono: String? = null,
    @SerialName("numero_licencia") val numeroLicencia: String? = null,
    val rol: String,
    val estado: String,
    val calificacion: Double? = null,
    @SerialName("vehiculo_id") val vehiculoId: Long? = null,
    @SerialName("vehiculo_placa") val vehiculoPlaca: String? = null
)

@Serializable
data class UsuarioPerfilActualizacion(
    @SerialName("nombre_completo") val nombreCompleto: String,
    val cedula: String,
    val telefono: String,
    @SerialName("numero_licencia") val numeroLicencia: String? = null
)
