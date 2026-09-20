package com.example.proyecto1ap.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.vehiculo.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleUsuarioState(
    val usuario: UsuarioListado? = null,
    val vehiculosDisponibles: List<Vehiculo> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null,
    val mensaje: String? = null,
    val mostrarAsignacion: Boolean = false
)

class DetalleUsuario(private val usuarioId: String) : ViewModel() {

    private val repo = UsuarioRepository()

    private val _state = MutableStateFlow(DetalleUsuarioState())
    val state = _state.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }

            val disponibles = repo.vehiculosDisponibles().getOrDefault(emptyList())

            repo.detalle(usuarioId)
                .onSuccess { u ->
                    _state.update {
                        it.copy(usuario = u, vehiculosDisponibles = disponibles, cargando = false)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, error = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }

    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }
    fun abrirAsignacion() = _state.update { it.copy(mostrarAsignacion = true) }
    fun cerrarAsignacion() = _state.update { it.copy(mostrarAsignacion = false) }

    fun cambiarEstado(nuevoEstado: String) {
        val u = _state.value.usuario ?: return
        viewModelScope.launch {
            repo.cambiarEstado(u.id, nuevoEstado)
                .onSuccess {
                    _state.update { it.copy(mensaje = "Cuenta marcada como ${nuevoEstado.lowercase()}") }
                    cargar()
                }
                .onFailure { e -> _state.update { it.copy(mensaje = "Error: ${e.message}") } }
        }
    }

    fun asignarVehiculo(vehiculoId: Long?) {
        val u = _state.value.usuario ?: return
        viewModelScope.launch {
            _state.update { it.copy(mostrarAsignacion = false) }
            repo.asignarVehiculo(u.id, vehiculoId)
                .onSuccess {
                    _state.update { it.copy(mensaje = "Asignación actualizada") }
                    cargar()
                }
                .onFailure { e -> _state.update { it.copy(mensaje = "Error: ${e.message}") } }
        }
    }
}

class DetalleUsuarioFactory(private val usuarioId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetalleUsuario(usuarioId) as T
    }
}