package com.example.proyecto1ap.reportes

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.VehiculoListado
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class ReportesRepository {

    suspend fun vehiculos(): Result<List<VehiculoListado>> = runCatching {
        SupabaseManager.client.from("vehiculos_listado")
            .select()
            .decodeList<VehiculoListado>()
    }

    suspend fun mantenimientos(f: FiltrosReporte): Result<List<MantenimientoReporte>> = runCatching {
        SupabaseManager.client.from("mantenimientos").select(
            Columns.raw(
                "id, tipo_mantenimiento, descripcion, fecha_mantenimiento, " +
                        "costo_aproximado, categoria_servicio, " +
                        "vehiculos(placa, marca, modelo), usuarios(nombre_completo)"
            )
        ) {
            filter {
                f.vehiculoId?.let { eq("vehiculo_id", it) }
                f.tipo?.let { eq("tipo_mantenimiento", it) }
                if (f.desde.isNotBlank()) gte("fecha_mantenimiento", f.desde)
                if (f.hasta.isNotBlank()) lte("fecha_mantenimiento", f.hasta)
                f.costoMin.toDoubleOrNull()?.let { gte("costo_aproximado", it) }
                f.costoMax.toDoubleOrNull()?.let { lte("costo_aproximado", it) }
            }
            order("fecha_mantenimiento", Order.DESCENDING)
            order("id", Order.DESCENDING)
        }.decodeList<MantenimientoReporte>()
    }
}