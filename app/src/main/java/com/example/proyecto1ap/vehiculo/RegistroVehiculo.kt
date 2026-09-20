package com.example.proyecto1ap.vehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.usuario.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroVehiculoState(
    val placa: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val color: String = "",
    val tipoVehiculo: String? = null,
    val tipoCombustible: String? = null,
    val capacidad: String = "",
    val conductorId: String? = null,
    val conductores: List<Usuario> = emptyList(),
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val exito: Boolean = false,
    val vencimientoMarchamo: String = "",
    val vencimientoRevisionTecnica: String = "",
    val vencimientoSeguro: String = "",
) {
    val puedeGuardar: Boolean
        get() = placa.isNotBlank() &&
                marca.isNotBlank() &&
                modelo.isNotBlank() &&
                (anio.toIntOrNull() ?: 0) in 1900..2100 &&
                color.isNotBlank() &&
                tipoVehiculo != null &&
                tipoCombustible != null &&
                !guardando
}

class RegistroVehiculo : ViewModel() {

    private val repo = VehiculoRepository()

    private val _state = MutableStateFlow(RegistroVehiculoState())
    val state = _state.asStateFlow()

    init {
        cargarConductores()
    }

    private fun cargarConductores() {
        viewModelScope.launch {
            repo.conductoresActivos()
                .onSuccess { lista -> _state.update { it.copy(conductores = lista) } }
                .onFailure { e ->
                    _state.update { it.copy(mensaje = "Error cargando conductores: ${e.message}") }
                }
        }
    }

    fun onPlaca(v: String) = _state.update { it.copy(placa = v.uppercase()) }
    fun onMarca(v: String) = _state.update { it.copy(marca = v) }
    fun onModelo(v: String) = _state.update { it.copy(modelo = v) }
    fun onAnio(v: String) = _state.update { it.copy(anio = v.filter(Char::isDigit).take(4)) }
    fun onColor(v: String) = _state.update { it.copy(color = v) }
    fun onCapacidad(v: String) = _state.update { it.copy(capacidad = v.filter(Char::isDigit)) }
    fun onTipoVehiculo(v: String) = _state.update { it.copy(tipoVehiculo = v) }
    fun onTipoCombustible(v: String) = _state.update { it.copy(tipoCombustible = v) }
    fun onConductor(id: String?) = _state.update { it.copy(conductorId = id) }
    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    fun onVenceMarchamo(v: String) = _state.update { it.copy(vencimientoMarchamo = v) }

    fun onVenceRevision(v: String) = _state.update { it.copy(vencimientoRevisionTecnica = v) }

    fun onVenceSeguro(v: String) = _state.update { it.copy(vencimientoSeguro = v) }

    fun guardar() {
        val s = _state.value
        if (!s.puedeGuardar) return

        viewModelScope.launch {
            _state.update { it.copy(guardando = true, mensaje = null) }

            val nuevo = VehiculoNuevo(
                placa = s.placa.trim(),
                marca = s.marca.trim(),
                modelo = s.modelo.trim(),
                anio = s.anio.toInt(),
                color = s.color.trim(),
                tipoVehiculo = s.tipoVehiculo!!,
                tipoCombustible = s.tipoCombustible!!,
                capacidad = s.capacidad.toIntOrNull(),
                conductorId = s.conductorId
            )

            repo.crear(nuevo)
                .onSuccess {
                    _state.update {
                        RegistroVehiculoState(
                            conductores = it.conductores,
                            mensaje = "Vehículo registrado",
                            exito = true
                        )
                    }
                }
                .onFailure { e ->
                    val msg = when {
                        e.message?.contains("duplicate key") == true ->
                            "Ya existe un vehículo con esa placa"
                        e.message?.contains("row-level security") == true ->
                            "No tenés permisos para registrar vehículos"
                        else -> "Error: ${e.message}"
                    }
                    _state.update { it.copy(guardando = false, mensaje = msg) }
                }
        }
    }
}