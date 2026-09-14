package com.example.proyecto1ap

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
    val estado: String = "ACTIVO"
)