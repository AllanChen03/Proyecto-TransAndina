package com.example.proyecto1ap.usuario

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.Vehiculo
import io.github.jan.supabase.auth.auth
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

    suspend fun cambiarCalificacion(id: String, calificacion: Double?): Result<Unit> = runCatching {
        SupabaseManager.client.from("usuarios").update(
            mapOf("calificacion" to calificacion)
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

    suspend fun detalle(id: String): Result<UsuarioListado> = runCatching {
        SupabaseManager.client.from("usuarios_listado").select {
            filter { eq("id", id) }
        }.decodeSingle<UsuarioListado>()
    }

    suspend fun miPerfil(): Result<Usuario> = runCatching {
        val id = SupabaseManager.client.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        SupabaseManager.client.from("usuarios").select {
            filter { eq("id", id) }
        }.decodeSingle<Usuario>()
    }

    suspend fun actualizarPerfil(datos: UsuarioPerfilActualizacion): Result<Unit> = runCatching {
        val id = SupabaseManager.client.auth.currentUserOrNull()?.id
            ?: error("No hay sesión activa")
        SupabaseManager.client.from("usuarios").update(datos) {
            filter { eq("id", id) }
        }
    }
}