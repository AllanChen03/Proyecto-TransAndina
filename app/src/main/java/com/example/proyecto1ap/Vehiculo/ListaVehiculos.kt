package com.example.proyecto1ap.Vehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaVehiculosState(
    val vehiculos: List<VehiculoListado> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class ListaVehiculos : ViewModel() {

    private val repo = VehiculoRepository()

    private val _state = MutableStateFlow(ListaVehiculosState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            repo.listar()
                .onSuccess { lista ->
                    _state.update { it.copy(vehiculos = lista, cargando = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }
}