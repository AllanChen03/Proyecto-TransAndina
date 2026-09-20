package com.example.proyecto1ap.vehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleVehiculoState(
    val vehiculo: VehiculoListado? = null,
    val cargando: Boolean = true,
    val error: String? = null
)

class DetalleVehiculo(private val vehiculoId: Long) : ViewModel() {

    private val repo = VehiculoRepository()

    private val _state = MutableStateFlow(DetalleVehiculoState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            repo.detalle(vehiculoId)
                .onSuccess { v ->
                    _state.update { it.copy(vehiculo = v, cargando = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }
}

class DetalleVehiculoFactory(private val vehiculoId: Long) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetalleVehiculo(vehiculoId) as T
    }
}