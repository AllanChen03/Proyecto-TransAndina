package com.example.proyecto1ap.mantenimiento

import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.usuario.Usuario
import com.example.proyecto1ap.vehiculo.Vehiculo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.hours
import io.github.jan.supabase.postgrest.query.Columns

class MantenimientoRepository {

    // ============================================
    // VEHÍCULOS
    // ============================================

    suspend fun vehiculosActivos(): Result<List<Vehiculo>> = runCatching {
        SupabaseManager.client.from("vehiculos").select {
            filter { eq("estado", "ACTIVO") }
        }.decodeList<Vehiculo>()
    }

    suspend fun vehiculoActivoPorConductor(conductorId: String): Result<List<Vehiculo>> = runCatching {
        SupabaseManager.client.from("vehiculos").select {
            filter {
                eq("estado", "ACTIVO")
                eq("conductor_id", conductorId)
            }
        }.decodeList<Vehiculo>()
    }

    /** Placa de un vehículo, para títulos de pantalla. */
    suspend fun placaDe(vehiculoId: Long): String? = runCatching {
        SupabaseManager.client.from("vehiculos").select {
            filter { eq("id", vehiculoId) }
        }.decodeSingle<Vehiculo>().placa
    }.getOrNull()

    // ============================================
    // REGISTRO
    // ============================================

    suspend fun insertar(mantenimiento: MantenimientoNuevo): Result<Long> = runCatching {
        SupabaseManager.client.from("mantenimientos")
            .insert(mantenimiento) { select() }
            .decodeSingle<MantenimientoFila>()
            .id
    }

    // ============================================
    // LISTADOS
    // ============================================

    suspend fun contarPorMecanico(mecanicoId: String): Result<Int> = runCatching {
        SupabaseManager.client.from("mantenimientos").select {
            filter { eq("mecanico_id", mecanicoId) }
        }.decodeList<MantenimientoFila>().size
    }

    suspend fun calcularCalificacion(): Float = 0f

    /** Mantenimientos registrados por un usuario. */
    suspend fun listarPorMecanico(mecanicoId: String): Result<List<MantenimientoListado>> =
        runCatching {
            val filas = SupabaseManager.client.from("mantenimientos").select {
                filter { eq("mecanico_id", mecanicoId) }
            }.decodeList<MantenimientoFila>()
            armarListado(filas)
        }

    /** Mantenimientos de un vehículo específico. */
    suspend fun listarPorVehiculo(vehiculoId: Long): Result<List<MantenimientoListado>> =
        runCatching {
            val filas = SupabaseManager.client.from("mantenimientos").select {
                filter { eq("vehiculo_id", vehiculoId) }
            }.decodeList<MantenimientoFila>()
            armarListado(filas)
        }

    /** Todos los mantenimientos de la flotilla. */
    suspend fun listarTodos(): Result<List<MantenimientoListado>> = runCatching {
        val filas = SupabaseManager.client.from("mantenimientos").select()
            .decodeList<MantenimientoFila>()
        armarListado(filas)
    }

    /** Convierte filas crudas en listado, resolviendo vehículo y quien registró. */
    private suspend fun armarListado(filas: List<MantenimientoFila>): List<MantenimientoListado> {
        if (filas.isEmpty()) return emptyList()

        val vehiculos = SupabaseManager.client.from("vehiculos").select()
            .decodeList<Vehiculo>()
            .associateBy { it.id }

        val usuarios = runCatching {
            SupabaseManager.client.from("usuarios").select()
                .decodeList<Usuario>()
                .associateBy { it.id }
        }.getOrDefault(emptyMap())

        return filas.map { fila ->
            val vehiculo = vehiculos[fila.vehiculoId]
            MantenimientoListado(
                id = fila.id,
                vehiculoPlaca = vehiculo?.placa ?: fila.vehiculoId.toString(),
                vehiculoDescripcion = vehiculo?.let { "${it.marca} ${it.modelo}" } ?: "",
                tipo = TipoMantenimiento.desde(fila.tipoMantenimiento),
                fecha = fila.fechaMantenimiento,
                titulo = etiquetaCategoria(fila.categoriaServicio) ?: fila.descripcion,
                categoriaServicio = etiquetaCategoria(fila.categoriaServicio),
                mecanico = fila.mecanicoId?.let { usuarios[it]?.nombreCompleto },
                costo = fila.costoAproximado
            )
        }.sortedByDescending { it.fecha }
    }

    // ============================================
    // DETALLE
    // ============================================

    suspend fun detalle(id: Long): Result<MantenimientoDetalle> = runCatching {
        val fila = SupabaseManager.client.from("mantenimientos").select {
            filter { eq("id", id) }
        }.decodeSingle<MantenimientoFila>()

        val vehiculo = runCatching {
            SupabaseManager.client.from("vehiculos").select {
                filter { eq("id", fila.vehiculoId) }
            }.decodeSingle<Vehiculo>()
        }.getOrNull()

        val usuario = fila.mecanicoId?.let { uid ->
            runCatching {
                SupabaseManager.client.from("usuarios").select {
                    filter { eq("id", uid) }
                }.decodeSingle<Usuario>()
            }.getOrNull()
        }

        MantenimientoDetalle(
            id = fila.id,
            vehiculoId = fila.vehiculoId,
            vehiculoPlaca = vehiculo?.placa ?: "—",
            vehiculoDescripcion = vehiculo?.let { "${it.marca} ${it.modelo} ${it.anio}" } ?: "",
            tipo = TipoMantenimiento.desde(fila.tipoMantenimiento),
            categoriaServicio = etiquetaCategoria(fila.categoriaServicio),
            fecha = fila.fechaMantenimiento,
            kilometraje = fila.kilometraje,
            descripcion = fila.descripcion,
            costo = fila.costoAproximado,
            taller = fila.taller,
            mecanicoNombre = usuario?.nombreCompleto
        )
    }

    // ============================================
    // EVIDENCIAS FOTOGRÁFICAS
    // ============================================

    /** Sube una foto al Storage y devuelve su ruta dentro del bucket. */
    suspend fun subirFoto(
        mantenimientoId: Long,
        bytes: ByteArray,
        nombre: String
    ): Result<String> = runCatching {
        val ruta = "$mantenimientoId/$nombre"
        SupabaseManager.client.storage.from("mantenimientos").upload(ruta, bytes) {
            upsert = true
        }
        ruta
    }.onFailure {
        android.util.Log.e("FOTOS", "Error subiendo foto: ${it.message}", it)
    }

    /** Registra en la base la referencia de una foto ya subida. */
    suspend fun guardarEvidencia(mantenimientoId: Long, urlImagen: String): Result<Unit> =
        runCatching {
            SupabaseManager.client.from("evidencias_fotograficas").insert(
                EvidenciaNueva(
                    mantenimientoId = mantenimientoId,
                    urlImagen = urlImagen,
                    publicId = urlImagen
                )
            )
            Unit
        }.onFailure {
            android.util.Log.e("FOTOS", "Error guardando evidencia: ${it.message}", it)
        }

    /** URLs firmadas de las fotos de un mantenimiento (válidas 2 horas). */
    suspend fun fotosDe(mantenimientoId: Long): Result<List<String>> = runCatching {
        val evidencias = SupabaseManager.client.from("evidencias_fotograficas").select {
            filter { eq("mantenimiento_id", mantenimientoId) }
        }.decodeList<EvidenciaFila>()

        android.util.Log.d("FOTOS", "Mantenimiento $mantenimientoId → ${evidencias.size} evidencias")

        val bucket = SupabaseManager.client.storage.from("mantenimientos")
        evidencias.mapNotNull { ev ->
            runCatching { bucket.createSignedUrl(ev.urlImagen, 2.hours) }
                .onSuccess { android.util.Log.d("FOTOS", "URL generada: $it") }
                .onFailure { android.util.Log.e("FOTOS", "Falló URL: ${it.message}", it) }
                .getOrNull()
        }
    }
    suspend fun actualizar(id: Long, datos: MantenimientoEditable): Result<Unit> = runCatching {
        SupabaseManager.client.from("mantenimientos").update(datos) {
            filter { eq("id", id) }
        }
        Unit
    }


    suspend fun eliminar(id: Long): Result<Unit> = runCatching {
        val evidencias = SupabaseManager.client.from("evidencias_fotograficas").select {
            filter { eq("mantenimiento_id", id) }
        }.decodeList<EvidenciaFila>()

        if (evidencias.isNotEmpty()) {
            runCatching {
                SupabaseManager.client.storage.from("mantenimientos")
                    .delete(evidencias.map { it.urlImagen })
            }
        }

        SupabaseManager.client.from("mantenimientos").delete {
            filter { eq("id", id) }
        }
        Unit
    }

    suspend fun eliminarEvidencia(evidenciaId: Long, ruta: String): Result<Unit> = runCatching {
        runCatching {
            SupabaseManager.client.storage.from("mantenimientos").delete(listOf(ruta))
        }
        SupabaseManager.client.from("evidencias_fotograficas").delete {
            filter { eq("id", evidenciaId) }
        }
        Unit
    }

    suspend fun evidenciasDe(mantenimientoId: Long): Result<List<FotoEvidencia>> = runCatching {
        val evidencias = SupabaseManager.client.from("evidencias_fotograficas").select {
            filter { eq("mantenimiento_id", mantenimientoId) }
        }.decodeList<EvidenciaFila>()

        val bucket = SupabaseManager.client.storage.from("mantenimientos")
        evidencias.mapNotNull { ev ->
            runCatching { bucket.createSignedUrl(ev.urlImagen, 2.hours) }
                .getOrNull()
                ?.let { FotoEvidencia(ev.id, ev.urlImagen, it) }
            }
    }
}
