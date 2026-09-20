package com.example.proyecto1ap.kilometraje

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.VehiculoListado
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
class KilometrajeRepository {

    /** Vehículo asignado al conductor (null si no tiene). */
    suspend fun vehiculoDelConductor(conductorId: String): Result<VehiculoListado?> = runCatching {
        SupabaseManager.client.from("vehiculos_listado").select {
            filter { eq("conductor_id", conductorId) }
        }.decodeList<VehiculoListado>().firstOrNull()
    }

    suspend fun registrar(nuevo: RegistroKilometrajeNuevo): Result<Unit> = runCatching {
        SupabaseManager.client.from("registros_kilometraje").insert(nuevo)
    }
    suspend fun vehiculoPorId(id: Long): Result<VehiculoListado?> = runCatching {
        SupabaseManager.client.from("vehiculos_listado").select {
            filter { eq("id", id) }
        }.decodeList<VehiculoListado>().firstOrNull()
    }

    suspend fun historial(vehiculoId: Long): Result<List<RegistroConUsuario>> = runCatching {
        SupabaseManager.client.from("registros_kilometraje").select(
            Columns.raw("id, kilometraje, fecha, conductor_id, usuarios(nombre_completo)")
        ) {
            filter { eq("vehiculo_id", vehiculoId) }
            order("fecha", Order.DESCENDING)
            order("id", Order.DESCENDING)
        }.decodeList<RegistroConUsuario>()
    }
}