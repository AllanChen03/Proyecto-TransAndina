package com.example.proyecto1ap.mantenimiento

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.usuario.Usuario
import com.example.proyecto1ap.vehiculo.Vehiculo
import com.example.proyecto1ap.vehiculo.VehiculoListado
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MantenimientoRepository {

    suspend fun vehiculosActivos(): Result<List<Vehiculo>> = runCatching {
        SupabaseManager.client.from("vehiculos").select {
            filter { eq("estado", "ACTIVO") }
        }.decodeList<Vehiculo>()
    }

    suspend fun insertar(mantenimiento: MantenimientoNuevo): Result<Long> = runCatching {
        SupabaseManager.client.from("mantenimientos")
            .insert(mantenimiento) { select() }
            .decodeSingle<MantenimientoFila>()
            .id
    }

    suspend fun contarPorMecanico(mecanicoId: String): Result<Int> = runCatching {
        SupabaseManager.client.from("mantenimientos").select {
            filter { eq("mecanico_id", mecanicoId) }
        }.decodeList<MantenimientoFila>().size
    }

    suspend fun calcularCalificacion(): Float = 0f

    suspend fun listarPorMecanico(mecanicoId: String): Result<List<MantenimientoListado>> = runCatching {
        val filas = SupabaseManager.client.from("mantenimientos").select {
            filter { eq("mecanico_id", mecanicoId) }
        }.decodeList<MantenimientoFila>()

        val vehiculos = SupabaseManager.client.from("vehiculos").select()
            .decodeList<Vehiculo>()
            .associateBy { it.id }

        val mecanicoNombre = runCatching {
            SupabaseManager.client.from("usuarios").select {
                filter { eq("id", mecanicoId) }
            }.decodeSingle<Usuario>().nombreCompleto
        }.getOrNull()

        filas.map { fila ->
            val vehiculo = vehiculos[fila.vehiculoId]
            MantenimientoListado(
                id = fila.id,
                vehiculoPlaca = vehiculo?.placa ?: fila.vehiculoId.toString(),
                vehiculoDescripcion = vehiculo?.let { "${it.marca} ${it.modelo}" } ?: "",
                tipo = TipoMantenimiento.desde(fila.tipoMantenimiento),
                fecha = fila.fechaMantenimiento,
                titulo = etiquetaCategoria(fila.categoriaServicio) ?: fila.descripcion,
                categoriaServicio = etiquetaCategoria(fila.categoriaServicio),
                mecanico = mecanicoNombre,
                costo = fila.costoAproximado
            )
        }.sortedByDescending { it.fecha }
    }

    suspend fun vehiculosEnTaller(): Result<List<ResumenVehiculo>> = runCatching {
        val abiertos = SupabaseManager.client.from("mantenimientos").select()
            .decodeList<MantenimientoFila>()
            .filter { it.fechaProximoMantenimiento == null }
            .groupBy { it.vehiculoId }
            .mapNotNull { (_, lista) -> lista.maxByOrNull { it.id } }
            .map { it.vehiculoId }
            .toSet()

        val vehiculos = SupabaseManager.client.from("vehiculos").select()
            .decodeList<Vehiculo>()

        vehiculos
            .filter { it.estado.equals("ACTIVO", ignoreCase = true) }
            .filter { it.id in abiertos }
            .map { it.aResumen() }
    }

    suspend fun vehiculosPendientes(): Result<List<ResumenVehiculo>> = runCatching {
        val listado = SupabaseManager.client.from("vehiculos_listado").select()
            .decodeList<VehiculoListado>()

        listado
            .filter { it.estado.equals("ACTIVO", ignoreCase = true) }
            .filter { esPendiente(it) }
            .map {
                ResumenVehiculo(
                    id = it.id,
                    placa = it.placa,
                    marcaModelo = "${it.marca} ${it.modelo}".trim(),
                    combustible = it.tipoCombustible
                )
            }
    }

    private fun esPendiente(v: VehiculoListado): Boolean {
        val hoy = LocalDate.now()
        val fechas = listOfNotNull(
            v.vencimientoMarchamo,
            v.vencimientoRevisionTecnica,
            v.vencimientoSeguro,
            v.proximoVencimiento
        ).mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }

        return fechas.any {
            !it.isAfter(hoy) || ChronoUnit.DAYS.between(hoy, it) <= 30
        }
    }

    private fun Vehiculo.aResumen() = ResumenVehiculo(
        id = id,
        placa = placa,
        marcaModelo = "$marca $modelo".trim(),
        combustible = tipoCombustible.toString()
    )

    suspend fun subirFoto(mantenimientoId: Long, bytes: ByteArray, nombre: String): Result<String> = runCatching {
        SupabaseManager.client.storage.from("mantenimientos").upload(
            "$mantenimientoId/$nombre",
            bytes
        ) { upsert = true }
        "$mantenimientoId/$nombre"
    }

    suspend fun guardarEvidencia(mantenimientoId: Long, urlImagen: String): Result<Unit> = runCatching {
        SupabaseManager.client.from("evidencias_fotograficas").insert(
            mapOf(
                "mantenimiento_id" to mantenimientoId,
                "url_imagen" to urlImagen,
                "public_id" to urlImagen
            )
        )
    }
}