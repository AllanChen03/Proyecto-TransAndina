package com.example.proyecto1ap.vehiculo

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.usuario.Usuario
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class MantenimientoResumenVehiculo(
    val id: Long,
    @SerialName("vehiculo_id") val vehiculoId: Long,
    @SerialName("fecha_mantenimiento") val fechaMantenimiento: String,
    @SerialName("categoria_servicio") val categoriaServicio: String? = null,
    val kilometraje: Int? = null,
    @SerialName("costo_aproximado") val costoAproximado: Double? = null
)

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
        val vehiculos = SupabaseManager.client.from("vehiculos_listado")
            .select()
            .decodeList<VehiculoListado>()

        val mantenimientos = runCatching { mantenimientosResumen() }
            .getOrDefault(emptyList())

        if (mantenimientos.isEmpty()) {
            vehiculos
        } else {
            vehiculos.map { it.conResumenMantenimientos(mantenimientos) }
        }
    }

    suspend fun detalle(id: Long): Result<VehiculoListado> = runCatching {
        val vehiculo = SupabaseManager.client.from("vehiculos_listado").select {
            filter { eq("id", id) }
        }.decodeSingle<VehiculoListado>()

        val mantenimientos = runCatching { mantenimientosResumen(id) }
            .getOrDefault(emptyList())

        if (mantenimientos.isEmpty()) {
            vehiculo
        } else {
            vehiculo.conResumenMantenimientos(mantenimientos)
        }
    }

    suspend fun vehiculoPorConductor(conductorId: String): Result<VehiculoListado?> = runCatching {
        SupabaseManager.client.from("vehiculos_listado").select {
            filter { eq("conductor_id", conductorId) }
        }.decodeList<VehiculoListado>().firstOrNull()
    }

    private suspend fun mantenimientosResumen(vehiculoId: Long? = null): List<MantenimientoResumenVehiculo> {
        return SupabaseManager.client.from("mantenimientos").select(
            Columns.raw(
                "id, vehiculo_id, fecha_mantenimiento, categoria_servicio, kilometraje, costo_aproximado"
            )
        ) {
            filter {
                vehiculoId?.let { eq("vehiculo_id", it) }
            }
        }.decodeList()
    }

    private fun VehiculoListado.conResumenMantenimientos(
        mantenimientos: List<MantenimientoResumenVehiculo>
    ): VehiculoListado {
        val propios = mantenimientos.filter { it.vehiculoId == id }
        if (propios.isEmpty()) return this

        val ultimo = propios.maxWithOrNull(
            compareBy<MantenimientoResumenVehiculo> { it.fechaMantenimiento }
                .thenBy { it.id }
        )

        return copy(
            totalMantenimientos = propios.size,
            costoTotalMantenimientos = propios.sumOf { it.costoAproximado ?: 0.0 },
            fechaUltimoMantenimiento = ultimo?.fechaMantenimiento,
            categoriaUltimoMantenimiento = ultimo?.categoriaServicio,
            kmUltimoMantenimiento = ultimo?.kilometraje
        )
    }
}
