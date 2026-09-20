package com.example.proyecto1ap.usuario

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
)@Serializable

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
    @SerialName("vehiculo_placa") val vehiculoPlaca: String? = null,
    @SerialName("vehiculo_marca") val vehiculoMarca: String? = null,
    @SerialName("vehiculo_modelo") val vehiculoModelo: String? = null,
    @SerialName("vehiculo_anio") val vehiculoAnio: Int? = null,
    @SerialName("vehiculo_combustible") val vehiculoCombustible: String? = null

)
@Serializable
data class UsuarioPerfilActualizacion(
    @SerialName("nombre_completo") val nombreCompleto: String,
    val telefono: String? = null,
    @SerialName("numero_licencia") val numeroLicencia: String? = null
)