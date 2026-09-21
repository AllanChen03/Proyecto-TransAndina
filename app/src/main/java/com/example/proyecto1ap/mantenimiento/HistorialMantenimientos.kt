package com.example.proyecto1ap.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HistorialMantenimientosState(
    val registros: List<MantenimientoListado> = emptyList(),
    val placaVehiculo: String? = null,
    val busqueda: String = "",
    val filtroTipo: TipoMantenimiento? = null,
    val filtroCategoria: String? = null,
    val desde: String = "",
    val hasta: String = "",
    val cargando: Boolean = true,
    val error: String? = null
) {
    val registrosFiltrados: List<MantenimientoListado>
        get() = registros.filter { r ->
            val pasaBusqueda = busqueda.isBlank() ||
                    r.vehiculoPlaca.contains(busqueda, ignoreCase = true) ||
                    r.vehiculoDescripcion.contains(busqueda, ignoreCase = true)

            val pasaTipo = filtroTipo == null || r.tipo == filtroTipo

            val pasaCategoria = filtroCategoria == null ||
                    r.categoriaServicio == etiquetaCategoria(filtroCategoria)

            val pasaFecha = if (desde.isBlank() && hasta.isBlank()) {
                true
            } else {
                val fecha = runCatching { LocalDate.parse(r.fecha) }.getOrNull()
                val desdeDate = runCatching { LocalDate.parse(desde) }.getOrNull()
                val hastaDate = runCatching { LocalDate.parse(hasta) }.getOrNull()
                fecha != null &&
                        (desdeDate == null || !fecha.isBefore(desdeDate)) &&
                        (hastaDate == null || !fecha.isAfter(hastaDate))
            }

            pasaBusqueda && pasaTipo && pasaCategoria && pasaFecha
        }

    val hayFiltrosActivos: Boolean
        get() = busqueda.isNotBlank() || filtroTipo != null ||
                filtroCategoria != null || desde.isNotBlank() || hasta.isNotBlank()
}

/**
 * Historial de mantenimientos.
 *
 * - vehiculoId != null: solo los de ese vehículo.
 * - vehiculoId == null y soloMios = true: los registrados por el usuario actual.
 * - vehiculoId == null y soloMios = false: todos los de la flotilla.
 */
class HistorialMantenimientos(
    private val vehiculoId: Long? = null,
    private val soloMios: Boolean = true
) : ViewModel() {

    private val repo = MantenimientoRepository()

    private val _state = MutableStateFlow(HistorialMantenimientosState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }

            val resultado = when {
                vehiculoId != null -> {
                    val placa = repo.placaDe(vehiculoId)
                    _state.update { it.copy(placaVehiculo = placa) }
                    repo.listarPorVehiculo(vehiculoId)
                }
                soloMios -> {
                    val uid = SupabaseManager.client.auth.currentUserOrNull()?.id
                    if (uid == null) {
                        _state.update { it.copy(cargando = false, error = "Sesión no válida") }
                        return@launch
                    }
                    repo.listarPorMecanico(uid)
                }
                else -> repo.listarTodos()
            }

            resultado
                .onSuccess { lista ->
                    _state.update { it.copy(registros = lista, cargando = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }

    fun onBusqueda(v: String) = _state.update { it.copy(busqueda = v) }

    fun onFiltroTipo(t: TipoMantenimiento?) = _state.update { it.copy(filtroTipo = t) }

    fun onFiltroCategoria(c: String?) = _state.update { it.copy(filtroCategoria = c) }

    fun onRango(desde: String, hasta: String) =
        _state.update { it.copy(desde = desde, hasta = hasta) }

    fun limpiarRango() = _state.update { it.copy(desde = "", hasta = "") }

    fun limpiarFiltros() = _state.update {
        it.copy(
            busqueda = "",
            filtroTipo = null,
            filtroCategoria = null,
            desde = "",
            hasta = ""
        )
    }
}

class HistorialMantenimientosFactory(
    private val vehiculoId: Long? = null,
    private val soloMios: Boolean = true
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistorialMantenimientos(vehiculoId, soloMios) as T
    }
}