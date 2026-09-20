package com.example.proyecto1ap.Vehiculo

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.usuario.Usuario
import io.github.jan.supabase.postgrest.from

class VehiculoRepository {

    suspend fun crear(vehiculo: VehiculoNuevo): Result<Unit> = runCatching {
        SupabaseManager.client.from("vehiculos").insert(vehiculo)
    }

    suspend fun conductoresActivos(): Result<List<Usuario>> = runCatching {
        SupabaseManager.client.from("usuarios").select {
            filter {
                eq("rol", "CONDUCTOR")
                eq("estado", "ACTIVO")
            }
        }.decodeList<Usuario>()
    }

    suspend fun obtenerPorId(id: Long): Result<Vehiculo> = runCatching {
        SupabaseManager.client.from("vehiculos").select {
            filter { eq("id", id) }
        }.decodeSingle<Vehiculo>()
    }

    suspend fun actualizar(id: Long, vehiculo: VehiculoEditable): Result<Unit> = runCatching {
        SupabaseManager.client.from("vehiculos").update(vehiculo) {
            filter { eq("id", id) }
        }
    }

    suspend fun listar(): Result<List<VehiculoListado>> = runCatching {
        SupabaseManager.client.from("vehiculos_listado")
            .select()
            .decodeList<VehiculoListado>()
    }
}