package com.example.proyecto1ap.kilometraje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.Vehiculo.VehiculoListado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RegistrarKilometrajeState(
    val vehiculo: VehiculoListado? = null,
    val ultimoKm: Int? = null,
    val kilometraje: String = "",
    val fecha: String = LocalDate.now().toString(),
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,
    val exito: Boolean = false
) {
    val kmMenorAlUltimo: Boolean
        get() {
            val km = kilometraje.toIntOrNull()
            val ultimo = ultimoKm
            return km != null && ultimo != null && km < (ultimo + 1)
        }

    val puedeGuardar: Boolean
        get() = vehiculo != null &&
                kilometraje.toIntOrNull() != null &&
                !kmMenorAlUltimo &&
                !guardando && !cargando
}

class RegistrarKilometraje(private val conductorId: String) : ViewModel() {

    private val repo = KilometrajeRepository()

    private val _state = MutableStateFlow(RegistrarKilometrajeState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            repo.vehiculoDelConductor(conductorId)
                .onSuccess { v ->
                    if (v == null) {
                        _state.update {
                            it.copy(
                                cargando = false,
                                error = "No tienes un vehículo asignado. Consulta con el encargado de flota."
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(vehiculo = v, ultimoKm = v.kilometrajeActual, cargando = false)
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

    fun onKilometraje(v: String) =
        _state.update { it.copy(kilometraje = v.filter(Char::isDigit).take(7)) }

    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    fun guardar() {
        val s = _state.value
        if (!s.puedeGuardar) return
        val vehiculo = s.vehiculo ?: return
        val km = s.kilometraje.toIntOrNull() ?: return

        viewModelScope.launch {
            _state.update { it.copy(guardando = true, mensaje = null) }

            val nuevo = RegistroKilometrajeNuevo(
                vehiculoId = vehiculo.id,
                conductorId = conductorId,
                kilometraje = km,
                fecha = s.fecha
            )

            repo.registrar(nuevo)
                .onSuccess {
                    _state.update {
                        it.copy(
                            guardando = false,
                            ultimoKm = km,
                            mensaje = "Kilometraje registrado",
                            exito = true
                        )
                    }
                }
                .onFailure { e ->
                    val msg = when {
                        e.message?.contains("row-level security") == true ->
                            "No tienes permisos para registrar kilometraje en este vehículo"
                        else -> "Error: ${e.message}"
                    }
                    _state.update { it.copy(guardando = false, mensaje = msg) }
                }
        }
    }
}

class RegistrarKilometrajeFactory(private val conductorId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrarKilometraje(conductorId) as T
    }
}