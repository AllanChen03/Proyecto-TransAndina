package com.example.proyecto1ap.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.Vehiculo.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GestionUsuariosState(
    val usuarios: List<UsuarioListado> = emptyList(),
    val vehiculos: List<Vehiculo> = emptyList(),
    val filtroRol: String? = null,
    val busquedaCedula: String = "",
    val cargando: Boolean = true,
    val mensaje: String? = null,
    val usuarioParaAsignar: UsuarioListado? = null,
    val usuarioParaCalificar: UsuarioListado? = null
) {
    val usuariosFiltrados: List<UsuarioListado>
        get() {
            val porRol = if (filtroRol == null) usuarios
            else usuarios.filter { it.rol == filtroRol }

            val textoBusqueda = busquedaCedula.trim()
            return if (textoBusqueda.isBlank()) {
                porRol
            } else {
                porRol.filter { it.cedula.contains(textoBusqueda, ignoreCase = true) }
            }
        }
}

class GestionUsuarios : ViewModel() {

    private val repo = UsuarioRepository()

    private val _state = MutableStateFlow(GestionUsuariosState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true) }

            val vehiculos = repo.vehiculosDisponibles().getOrDefault(emptyList())

            repo.listar()
                .onSuccess { lista ->
                    _state.update {
                        it.copy(usuarios = lista, vehiculos = vehiculos, cargando = false)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, mensaje = "Error al cargar: ${e.message}")
                    }
                }
        }
    }

    fun onFiltro(rol: String?) = _state.update { it.copy(filtroRol = rol) }
    fun onBusquedaCedula(valor: String) = _state.update { it.copy(busquedaCedula = valor) }
    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    fun abrirAsignacion(usuario: UsuarioListado) =
        _state.update { it.copy(usuarioParaAsignar = usuario) }

    fun cerrarAsignacion() =
        _state.update { it.copy(usuarioParaAsignar = null) }

    fun abrirCalificacion(usuario: UsuarioListado) =
        _state.update { it.copy(usuarioParaCalificar = usuario) }

    fun cerrarCalificacion() =
        _state.update { it.copy(usuarioParaCalificar = null) }

    fun cambiarEstado(usuario: UsuarioListado, nuevoEstado: String) {
        viewModelScope.launch {
            repo.cambiarEstado(usuario.id, nuevoEstado)
                .onSuccess {
                    _state.update { st ->
                        st.copy(
                            usuarios = st.usuarios.map {
                                if (it.id == usuario.id) it.copy(estado = nuevoEstado) else it
                            },
                            mensaje = "Cuenta ${nuevoEstado.lowercase()}"
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(mensaje = "Error: ${e.message}") }
                }
        }
    }

    fun asignarVehiculo(conductorId: String, vehiculoId: Long?) {
        viewModelScope.launch {
            repo.asignarVehiculo(conductorId, vehiculoId)
                .onSuccess {
                    _state.update { it.copy(usuarioParaAsignar = null, mensaje = "Asignación actualizada") }
                    cargar()
                }
                .onFailure { e ->
                    _state.update { it.copy(mensaje = "Error: ${e.message}") }
                }
        }
    }

    fun cambiarCalificacion(usuario: UsuarioListado, calificacion: Double?) {
        viewModelScope.launch {
            repo.cambiarCalificacion(usuario.id, calificacion)
                .onSuccess {
                    _state.update { st ->
                        st.copy(
                            usuarioParaCalificar = null,
                            usuarios = st.usuarios.map {
                                if (it.id == usuario.id) {
                                    it.copy(calificacion = calificacion)
                                } else {
                                    it
                                }
                            },
                            mensaje = "Calificación actualizada"
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(mensaje = "Error: ${e.message}") }
                }
        }
    }
}
