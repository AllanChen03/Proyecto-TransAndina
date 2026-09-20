package com.example.proyecto1ap.kilometraje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.Vehiculo.VehiculoListado
import com.example.proyecto1ap.alertas.AlertasRepository
import com.example.proyecto1ap.alertas.ProximoMantenimiento
import com.example.proyecto1ap.alertas.calcularProximoMantenimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialKilometrajeState(
    val vehiculo: VehiculoListado? = null,
    val registros: List<RegistroConUsuario> = emptyList(),
    val proximo: ProximoMantenimiento? = null,
    val cargando: Boolean = true,
    val error: String? = null
)

/**
 * Si vehiculoId es null, se busca el vehículo asignado al conductor.
 */
class HistorialKilometraje(
    private val vehiculoId: Long?,
    private val conductorId: String?
) : ViewModel() {

    private val repo = KilometrajeRepository()

    private val _state = MutableStateFlow(HistorialKilometrajeState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }

            val resultado: Result<VehiculoListado?> = when {
                vehiculoId != null -> repo.vehiculoPorId(vehiculoId)
                conductorId != null -> repo.vehiculoDelConductor(conductorId)
                else -> Result.success(null)
            }

            resultado
                .onSuccess { v ->
                    if (v == null) {
                        _state.update {
                            it.copy(cargando = false, error = "No hay un vehículo asignado.")
                        }
                    } else {
                        val config = AlertasRepository().configuracion()
                        repo.historial(v.id)
                            .onSuccess { lista ->
                                _state.update {
                                    it.copy(
                                        vehiculo = v,
                                        registros = lista,
                                        proximo = calcularProximoMantenimiento(v, config),
                                        cargando = false
                                    )
                                }
                            }
                            .onFailure { e ->
                                _state.update {
                                    it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                                }
                            }
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

class HistorialKilometrajeFactory(
    private val vehiculoId: Long?,
    private val conductorId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistorialKilometraje(vehiculoId, conductorId) as T
    }
}