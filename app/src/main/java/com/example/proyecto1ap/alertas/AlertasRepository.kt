package com.example.proyecto1ap.alertas

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.VehiculoListado
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.time.Instant
import java.time.temporal.ChronoUnit
class AlertasRepository {

    /** Si falla o no existe la tabla, se usan los valores por defecto. */
    suspend fun configuracion(): ConfiguracionMantenimiento =
        runCatching {
            SupabaseManager.client.from("configuracion_mantenimiento")
                .select()
                .decodeList<ConfiguracionMantenimiento>()
                .firstOrNull()
        }.getOrNull() ?: ConfiguracionMantenimiento()

    /** conductorId != null: solo su vehículo. null: toda la flotilla. */
    suspend fun vehiculos(conductorId: String?): Result<List<VehiculoListado>> = runCatching {
        SupabaseManager.client.from("vehiculos_listado").select {
            if (conductorId != null) {
                filter { eq("conductor_id", conductorId) }
            }
        }.decodeList<VehiculoListado>()
    }
    suspend fun notificaciones(usuarioId: String): List<NotificacionFila> =
        runCatching {
            val desde = Instant.now().minus(7, ChronoUnit.DAYS).toString()
            SupabaseManager.client.from("alertas").select {
                filter {
                    eq("usuario_id", usuarioId)
                    gte("created_at", desde)
                }
                order("created_at", Order.DESCENDING)
            }.decodeList<NotificacionFila>()
        }.getOrDefault(emptyList())
}