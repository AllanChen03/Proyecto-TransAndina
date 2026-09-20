package com.example.proyecto1ap.kilometraje

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.Vehiculo.VehiculoListado
import io.github.jan.supabase.postgrest.from

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
}