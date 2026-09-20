package com.example.proyecto1ap.mantenimiento

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlertaFila(
    val id: Long,
    @SerialName("vehiculo_id") val vehiculoId: Long? = null,
    @SerialName("usuario_id") val usuarioId: String? = null,
    val tipo: String,
    val titulo: String,
    val mensaje: String,
    val prioridad: String,
    @SerialName("created_at") val createdAt: String? = null
)

data class AlertaListado(
    val id: Long,
    val tipo: String,
    val prioridad: String,
    val vehiculoPlaca: String? = null,
    val titulo: String,
    val mensaje: String
)