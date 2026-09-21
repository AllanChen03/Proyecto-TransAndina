package com.example.proyecto1ap.mantenimiento

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.vehiculo.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel

data class EditarMantenimientoState(
    val id: Long = 0,
    val tipo: TipoMantenimiento = TipoMantenimiento.PREVENTIVO,
    val vehiculos: List<Vehiculo> = emptyList(),
    val vehiculoId: Long? = null,
    val fecha: String = "",
    val categoriaServicio: String? = null,
    val kilometraje: String = "",
    val descripcion: String = "",
    val costo: String = "",
    val taller: String = "",
    val fotosExistentes: List<FotoEvidencia> = emptyList(),
    val fotosNuevas: List<Uri> = emptyList(),
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val guardadoExitoso: Boolean = false
) {
    val faltantes: List<String>
        get() = buildList {
            if (vehiculoId == null) add("vehículo")
            if (fecha.isBlank()) add("fecha")
            if (categoriaServicio == null) add("categoría")
            if (kilometraje.isBlank()) add("kilometraje")
            if (descripcion.trim().length < MIN_DESCRIPCION) add("descripción")
        }

    val puedeGuardar: Boolean
        get() = faltantes.isEmpty() && !guardando && !cargando
}

class EditarMantenimiento(
    application: Application,
    private val mantenimientoId: Long
) : AndroidViewModel(application) {

    private val repo = MantenimientoRepository()

    private val _state = MutableStateFlow(EditarMantenimientoState())
    val state = _state.asStateFlow()

    init { cargar() }

    private fun cargar() {
        viewModelScope.launch {
            val vehiculos = repo.vehiculosActivos().getOrDefault(emptyList())
            val fotos = repo.evidenciasDe(mantenimientoId).getOrDefault(emptyList())

            repo.detalle(mantenimientoId)
                .onSuccess { m ->
                    val categoriaValor = CATEGORIAS_SERVICIO
                        .firstOrNull { it.second == m.categoriaServicio }?.first

                    _state.update {
                        it.copy(
                            id = m.id,
                            tipo = m.tipo,
                            vehiculos = vehiculos,
                            vehiculoId = m.vehiculoId,
                            fecha = m.fecha,
                            categoriaServicio = categoriaValor,
                            kilometraje = m.kilometraje?.toString() ?: "",
                            descripcion = m.descripcion,
                            costo = m.costo?.toLong()?.toString() ?: "",
                            taller = m.taller ?: "",
                            fotosExistentes = fotos,
                            cargando = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, mensaje = "No se pudo cargar: ${e.message}")
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

    fun limpiarGuardado() = _state.update { it.copy(guardadoExitoso = false) }
    fun onTaller(v: String) = _state.update { it.copy(taller = v) }
    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    fun onFotosAgregar(nuevas: List<Uri>) =
        _state.update { it.copy(fotosNuevas = (it.fotosNuevas + nuevas).distinct()) }

    fun onQuitarFotoNueva(uri: Uri) =
        _state.update { it.copy(fotosNuevas = it.fotosNuevas - uri) }

    /** Borra una foto ya guardada, del Storage y de la base. */
    fun eliminarFotoExistente(foto: FotoEvidencia) {
        viewModelScope.launch {
            repo.eliminarEvidencia(foto.id, foto.ruta)
                .onSuccess {
                    _state.update {
                        it.copy(
                            fotosExistentes = it.fotosExistentes - foto,
                            mensaje = "Foto eliminada"
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(mensaje = "No se pudo eliminar: ${e.message}") }
                }
        }
    }

    fun guardar() {
        val s = _state.value
        if (!s.puedeGuardar) return

        viewModelScope.launch {
            _state.update { it.copy(guardando = true, mensaje = null) }

            val datos = MantenimientoEditable(
                vehiculoId = s.vehiculoId!!,
                tipoMantenimiento = s.tipo.name,
                categoriaServicio = s.categoriaServicio!!,
                fechaMantenimiento = s.fecha,
                kilometraje = s.kilometraje.toIntOrNull(),
                descripcion = s.descripcion.trim(),
                costoAproximado = s.costo.toDoubleOrNull(),
                taller = s.taller.trim().ifBlank { null }
            )

            repo.actualizar(s.id, datos)
                .onSuccess {
                    val fotosOk = if (s.fotosNuevas.isEmpty()) true
                    else subirFotos(s.id, s.fotosNuevas)
                    _state.update {
                        it.copy(
                            guardando = false,
                            guardadoExitoso = true,
                            mensaje = if (fotosOk) null
                            else "Cambios guardados, pero falló subir algunas fotos"
                        )
                    }
                }
                .onFailure { e ->
                    val msg = when {
                        e.message?.contains("row-level security") == true ->
                            "Solo el encargado de flota puede editar mantenimientos"
                        else -> "Error: ${e.message}"
                    }
                    _state.update { it.copy(guardando = false, mensaje = msg) }
                }
        }
    }

    private suspend fun subirFotos(mantenimientoId: Long, fotos: List<Uri>): Boolean {
        val resolver = getApplication<Application>().contentResolver
        var ok = true
        val desplazamiento = _state.value.fotosExistentes.size

        fotos.forEachIndexed { i, uri ->
            val bytes = runCatching {
                resolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()

            if (bytes == null) {
                ok = false
                return@forEachIndexed
            }

            val nombre = "foto_${desplazamiento + i + 1}_${System.currentTimeMillis()}.jpg"
            val subida = repo.subirFoto(mantenimientoId, bytes, nombre)
            if (subida.isFailure) {
                ok = false
                return@forEachIndexed
            }

            if (repo.guardarEvidencia(mantenimientoId, subida.getOrThrow()).isFailure) {
                ok = false
            }
        }
        return ok
    }
}

class EditarMantenimientoFactory(
    private val application: Application,
    private val mantenimientoId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditarMantenimiento(application, mantenimientoId) as T
    }
}