package com.example.proyecto1ap.mantenimiento

import androidx.lifecycle.ViewModel
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
    val filtroTipo: TipoMantenimiento? = null,
    val desde: String = "",
    val hasta: String = "",
    val cargando: Boolean = true,
    val error: String? = null
) {
    val registrosFiltrados: List<MantenimientoListado>
        get() = registros.filter { r ->
            val pasaTipo = filtroTipo == null || r.tipo == filtroTipo
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
            pasaTipo && pasaFecha
        }
}

class HistorialMantenimientos : ViewModel() {

    private val repo = MantenimientoRepository()

    private val _state = MutableStateFlow(HistorialMantenimientosState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            val uid = SupabaseManager.client.auth.currentUserOrNull()?.id
            if (uid == null) {
                _state.update { it.copy(cargando = false, error = "Sesión no válida") }
                return@launch
            }
            _state.update { it.copy(cargando = true, error = null) }
            repo.listarPorMecanico(uid)
                .onSuccess { lista -> _state.update { it.copy(registros = lista, cargando = false) } }
                .onFailure { e -> _state.update { it.copy(cargando = false, error = "No se pudo cargar: ${e.message}") } }
        }
    }

    fun onFiltroTipo(t: TipoMantenimiento) = _state.update { it.copy(filtroTipo = t) }

    fun limpiarFiltroTipo() = _state.update { it.copy(filtroTipo = null) }

    fun onRango(desde: String, hasta: String) = _state.update { it.copy(desde = desde, hasta = hasta) }

    fun limpiarRango() = _state.update { it.copy(desde = "", hasta = "") }
}