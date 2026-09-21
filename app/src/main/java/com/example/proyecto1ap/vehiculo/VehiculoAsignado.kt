package com.example.proyecto1ap.vehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehiculoAsignadoState(
    val vehiculo: VehiculoListado? = null,
    val cargando: Boolean = true,
    val error: String? = null
)

class VehiculoAsignado(private val conductorId: String) : ViewModel() {
    private val repo = VehiculoRepository()

    private val _state = MutableStateFlow(VehiculoAsignadoState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }

            repo.vehiculoPorConductor(conductorId)
                .onSuccess { vehiculo ->
                    _state.update {
                        it.copy(
                            vehiculo = vehiculo,
                            cargando = false,
                            error = if (vehiculo == null) "No tiene vehículo asignado" else null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            cargando = false,
                            error = "No se pudo cargar el vehículo: ${e.message}"
                        )
                    }
                }
        }
    }
}

class VehiculoAsignadoFactory(private val conductorId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VehiculoAsignado(conductorId) as T
    }
}
