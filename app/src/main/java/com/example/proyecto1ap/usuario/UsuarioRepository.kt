package com.example.proyecto1ap.usuario

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.Vehiculo
import io.github.jan.supabase.postgrest.from

class UsuarioRepository {

    suspend fun listar(): Result<List<UsuarioListado>> = runCatching {
        SupabaseManager.client.from("usuarios_listado")
            .select()
            .decodeList<UsuarioListado>()
    }

    suspend fun cambiarEstado(id: String, nuevoEstado: String): Result<Unit> = runCatching {
        SupabaseManager.client.from("usuarios").update(
            mapOf("estado" to nuevoEstado)
        ) {
            filter { eq("id", id) }
        }
    }

    suspend fun vehiculosDisponibles(): Result<List<Vehiculo>> = runCatching {
        SupabaseManager.client.from("vehiculos").select {
            filter { eq("estado", "ACTIVO") }
        }.decodeList<Vehiculo>()
            .filter { it.conductorId == null }
    }

    /**
     * Asigna un conductor a un vehículo. Primero lo libera de cualquier otro
     * vehículo, porque un conductor maneja solo uno.
     */
    suspend fun asignarVehiculo(conductorId: String, vehiculoId: Long?): Result<Unit> = runCatching {
        SupabaseManager.client.from("vehiculos").update(
            mapOf("conductor_id" to null)
        ) {
            filter { eq("conductor_id", conductorId) }
        }

        if (vehiculoId != null) {
            SupabaseManager.client.from("vehiculos").update(
                mapOf("conductor_id" to conductorId)
            ) {
                filter { eq("id", vehiculoId) }
            }
        }
    }
}