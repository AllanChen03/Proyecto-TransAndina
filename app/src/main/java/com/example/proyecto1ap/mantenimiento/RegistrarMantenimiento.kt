package com.example.proyecto1ap.mantenimiento

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.vehiculo.Vehiculo
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val MIN_DESCRIPCION = 10

data class RegistrarMantenimientoState(
    val tipo: TipoMantenimiento = TipoMantenimiento.PREVENTIVO,
    val vehiculos: List<Vehiculo> = emptyList(),
    val vehiculoId: Long? = null,
    val vehiculoBloqueado: Boolean = false,
    val fecha: String = "",
    val categoriaServicio: String? = null,
    val kilometraje: String = "",
    val descripcion: String = "",
    val costo: String = "",
    val taller: String = "",
    val fotos: List<Uri> = emptyList(),
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val exito: Boolean = false
) {
    /** Campos obligatorios que aún faltan completar. */
    val faltantes: List<String>
        get() = buildList {
            if (vehiculoId == null) add("vehículo")
            if (fecha.isBlank()) add("fecha")
            if (categoriaServicio == null) add("categoría")
            if (kilometraje.isBlank()) add("kilometraje")
            if (descripcion.trim().length < MIN_DESCRIPCION) add("descripción")
        }

    val puedeGuardar: Boolean
        get() = faltantes.isEmpty() && !guardando
}

class RegistrarMantenimiento(application: Application) : AndroidViewModel(application) {

    private val repo = MantenimientoRepository()

    private val _state = MutableStateFlow(RegistrarMantenimientoState())
    val state = _state.asStateFlow()

    init {
        cargarVehiculos()
    }

    private fun cargarVehiculos() {
        viewModelScope.launch {
            repo.vehiculosActivos()
                .onSuccess { lista -> _state.update { it.copy(vehiculos = lista) } }
                .onFailure { e ->
                    _state.update {
                        it.copy(mensaje = "Error cargando vehículos: ${e.message}")
                    }
                }
        }
    }

    fun cargarVehiculoDelConductor(conductorId: String?) {
        if (conductorId == null) {
            cargarVehiculos()
            _state.update { it.copy(vehiculoBloqueado = false, vehiculoId = null) }
            return
        }

        viewModelScope.launch {
            repo.vehiculoActivoPorConductor(conductorId)
                .onSuccess { lista ->
                    _state.update {
                        it.copy(
                            vehiculos = lista,
                            vehiculoId = lista.firstOrNull()?.id,
                            vehiculoBloqueado = true,
                            mensaje = if (lista.isEmpty()) {
                                "No tenés un vehículo activo asignado"
                            } else {
                                null
                            }
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            vehiculos = emptyList(),
                            vehiculoId = null,
                            vehiculoBloqueado = true,
                            mensaje = "Error cargando vehículo asignado: ${e.message}"
                        )
                    }
                }
        }
    }

    fun onTipo(t: TipoMantenimiento) = _state.update { it.copy(tipo = t) }

    fun onVehiculo(id: Long) = _state.update { it.copy(vehiculoId = id) }

    fun onFecha(v: String) = _state.update { it.copy(fecha = v) }

    fun onCategoriaServicio(v: String?) = _state.update { it.copy(categoriaServicio = v) }

    fun onKilometraje(v: String) = _state.update { it.copy(kilometraje = v.filter(Char::isDigit)) }

    fun onDescripcion(v: String) = _state.update { it.copy(descripcion = v) }

    fun onCosto(v: String) = _state.update {
        it.copy(costo = v.filter { c -> c.isDigit() || c == '.' })
    }

    fun onTaller(v: String) = _state.update { it.copy(taller = v) }

    fun onFotosAgregar(nuevas: List<Uri>) =
        _state.update { it.copy(fotos = (it.fotos + nuevas).distinct()) }

    fun onQuitarFoto(uri: Uri) = _state.update { it.copy(fotos = it.fotos - uri) }

    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    /** Limpia el formulario para cargar otro mantenimiento, sin recargar vehículos. */
    fun nuevoRegistro() = _state.update {
        RegistrarMantenimientoState(
            vehiculos = it.vehiculos,
            vehiculoId = if (it.vehiculoBloqueado) it.vehiculos.firstOrNull()?.id else null,
            vehiculoBloqueado = it.vehiculoBloqueado
        )
    }

    fun guardar() {
        val s = _state.value
        if (!s.puedeGuardar) return

        viewModelScope.launch {
            _state.update { it.copy(guardando = true, mensaje = null) }

            val uid = SupabaseManager.client.auth.currentUserOrNull()?.id
            if (uid == null) {
                _state.update {
                    it.copy(guardando = false, mensaje = "No se pudo identificar al usuario")
                }
                return@launch
            }

            val nuevo = MantenimientoNuevo(
                vehiculoId = s.vehiculoId!!,
                tipoMantenimiento = s.tipo.name,
                categoriaServicio = s.categoriaServicio!!,
                fechaMantenimiento = s.fecha,
                kilometraje = s.kilometraje.toIntOrNull(),
                descripcion = s.descripcion.trim(),
                costoAproximado = s.costo.toDoubleOrNull(),
                taller = s.taller.trim().ifBlank { null },
                mecanicoId = uid
            )

            val resultado = repo.insertar(nuevo)
            if (resultado.isSuccess) {
                val id = resultado.getOrNull() ?: return@launch
                val fotosOk = if (s.fotos.isEmpty()) true else subirFotos(id, s.fotos)
                _state.update {
                    it.copy(
                        guardando = false,
                        exito = true,
                        mensaje = if (fotosOk) null
                        else "Mantenimiento guardado, pero falló subir algunas fotos"
                    )
                }
            } else {
                val e = resultado.exceptionOrNull()
                val msg = when {
                    e?.message?.contains("row-level security") == true ->
                        "No tenés permisos para registrar mantenimientos"
                    else -> "Error: ${e?.message}"
                }
                _state.update { it.copy(guardando = false, mensaje = msg) }
            }
        }
    }

    private suspend fun subirFotos(mantenimientoId: Long, fotos: List<Uri>): Boolean {
        val resolver = getApplication<Application>().contentResolver
        var ok = true

        fotos.forEachIndexed { i, uri ->
            val bytes = runCatching {
                resolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()

            if (bytes == null) {
                ok = false
                return@forEachIndexed
            }

            val subida = repo.subirFoto(mantenimientoId, bytes, "foto_${i + 1}.jpg")
            if (subida.isFailure) {
                ok = false
                return@forEachIndexed
            }

            val ruta = subida.getOrThrow()
            if (repo.guardarEvidencia(mantenimientoId, ruta).isFailure) {
                ok = false
            }
        }
        return ok
    }
}
