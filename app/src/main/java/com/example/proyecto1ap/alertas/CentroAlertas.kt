package com.example.proyecto1ap.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CentroAlertasState(
    val alertas: List<AlertaCalculada> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class CentroAlertas(private val conductorId: String?) : ViewModel() {

    private val repo = AlertasRepository()

    private val _state = MutableStateFlow(CentroAlertasState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            val config = repo.configuracion()
            repo.vehiculos(conductorId)
                .onSuccess { lista ->
                    _state.update {
                        it.copy(alertas = calcularAlertas(lista, config), cargando = false)
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

class CentroAlertasFactory(private val conductorId: String?) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CentroAlertas(conductorId) as T
    }
}