package com.example.proyecto1ap.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.Vehiculo.VehiculoListado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReporteState(
    val filtros: FiltrosReporte = FiltrosReporte(),
    val vehiculos: List<VehiculoListado> = emptyList(),
    val resultados: List<MantenimientoReporte> = emptyList(),
    val cargando: Boolean = true,
    val aviso: String? = null,
    val error: String? = null
) {
    val totalCosto: Double
        get() = resultados.sumOf { it.costo ?: 0.0 }
}

class Reporte : ViewModel() {

    private val repo = ReportesRepository()

    private val _state = MutableStateFlow(ReporteState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.vehiculos().onSuccess { lista ->
                _state.update { it.copy(vehiculos = lista) }
            }
        }
        consultar()
    }

    fun onVehiculo(id: Long?) =
        _state.update { it.copy(filtros = it.filtros.copy(vehiculoId = id)) }

    fun onTipo(tipo: String?) =
        _state.update { it.copy(filtros = it.filtros.copy(tipo = tipo)) }

    fun onDesde(v: String) =
        _state.update { it.copy(filtros = it.filtros.copy(desde = v)) }

    fun onHasta(v: String) =
        _state.update { it.copy(filtros = it.filtros.copy(hasta = v)) }

    fun onCostoMin(v: String) =
        _state.update { it.copy(filtros = it.filtros.copy(costoMin = v.filter(Char::isDigit))) }

    fun onCostoMax(v: String) =
        _state.update { it.copy(filtros = it.filtros.copy(costoMax = v.filter(Char::isDigit))) }

    fun limpiar() {
        _state.update { it.copy(filtros = FiltrosReporte()) }
        consultar()
    }

    fun consultar() {
        val f = _state.value.filtros

        if (f.desde.isNotBlank() && f.hasta.isNotBlank() && f.desde > f.hasta) {
            _state.update { it.copy(aviso = "La fecha inicial no puede ser posterior a la fecha final.") }
            return
        }
        val min = f.costoMin.toDoubleOrNull()
        val max = f.costoMax.toDoubleOrNull()
        if (min != null && max != null && min > max) {
            _state.update { it.copy(aviso = "El costo mínimo no puede ser mayor al costo máximo.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(cargando = true, aviso = null, error = null) }
            repo.mantenimientos(f)
                .onSuccess { lista ->
                    _state.update { it.copy(resultados = lista, cargando = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }
}