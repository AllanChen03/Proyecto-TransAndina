package com.example.proyecto1ap.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleMantenimientoState(
    val mantenimiento: MantenimientoDetalle? = null,
    val fotos: List<String> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class DetalleMantenimiento(private val mantenimientoId: Long) : ViewModel() {

    private val repo = MantenimientoRepository()

    private val _state = MutableStateFlow(DetalleMantenimientoState())
    val state = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }

            repo.detalle(mantenimientoId)
                .onSuccess { m ->
                    val fotos = repo.fotosDe(mantenimientoId).getOrDefault(emptyList())
                    _state.update {
                        it.copy(mantenimiento = m, fotos = fotos, cargando = false)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }
}

class DetalleMantenimientoFactory(
    private val mantenimientoId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetalleMantenimiento(mantenimientoId) as T
    }
}