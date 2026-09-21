package com.example.proyecto1ap.mantenimiento

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.Vehiculo
import io.github.jan.supabase.postgrest.from

class AlertaRepository {

    suspend fun listarParaMecanico(usuarioId: String): Result<List<AlertaListado>> = runCatching {
        val filas = SupabaseManager.client.from("alertas").select {
            filter { eq("usuario_id", usuarioId) }
        }.decodeList<AlertaFila>()

        val vehiculos = SupabaseManager.client.from("vehiculos").select()
            .decodeList<Vehiculo>()
            .associateBy { it.id }

        filas.map {
            AlertaListado(
                id = it.id,
                tipo = it.tipo,
                prioridad = it.prioridad,
                vehiculoPlaca = it.vehiculoId?.let { id -> vehiculos[id]?.placa },
                titulo = it.titulo,
                mensaje = it.mensaje
            )
        }
    }

    suspend fun contarPara(usuarioId: String): Int =
        SupabaseManager.client.from("alertas").select {
            filter { eq("usuario_id", usuarioId) }
        }.decodeList<AlertaFila>().size
}