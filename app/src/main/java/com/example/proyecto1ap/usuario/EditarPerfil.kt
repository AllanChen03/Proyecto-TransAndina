package com.example.proyecto1ap.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditarPerfilState(
    val nombreCompleto: String = "",
    val cedula: String = "",
    val correo: String = "",
    val telefono: String = "",
    val numeroLicencia: String = "",
    val rol: String = "",
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val guardadoExitoso: Boolean = false
) {
    val esConductor: Boolean get() = rol == "CONDUCTOR"

    val puedeGuardar: Boolean
        get() = nombreCompleto.isNotBlank() && !guardando && !cargando
}

class EditarPerfil : ViewModel() {

    private val repo = UsuarioRepository()

    private val _state = MutableStateFlow(EditarPerfilState())
    val state = _state.asStateFlow()

    private var original: EditarPerfilState? = null

    init { cargar() }

    private fun cargar() {
        viewModelScope.launch {
            repo.miPerfil()
                .onSuccess { u ->
                    _state.update {
                        it.copy(
                            nombreCompleto = u.nombreCompleto,
                            cedula = u.cedula,
                            correo = u.correo,
                            telefono = u.telefono ?: "",
                            numeroLicencia = u.numeroLicencia ?: "",
                            rol = u.rol,
                            cargando = false
                        )
                    }
                    original = _state.value
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(cargando = false, mensaje = "No se pudo cargar: ${e.message}")
                    }
                }
        }
    }

    fun onNombre(v: String) = _state.update { it.copy(nombreCompleto = v) }
    fun onTelefono(v: String) = _state.update { it.copy(telefono = v) }
    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    fun hayCambios(): Boolean {
        val o = original ?: return false
        val s = _state.value
        return o.nombreCompleto != s.nombreCompleto || o.telefono != s.telefono
    }

    fun guardar() {
        val s = _state.value
        if (!s.puedeGuardar) return

        viewModelScope.launch {
            _state.update { it.copy(guardando = true, mensaje = null) }

            val datos = PerfilEditable(
                nombreCompleto = s.nombreCompleto.trim(),
                telefono = s.telefono.trim().ifBlank { null }
            )

            repo.actualizarPerfil(datos)
                .onSuccess {
                    _state.update {
                        it.copy(
                            guardando = false,
                            mensaje = "Perfil actualizado",
                            guardadoExitoso = true
                        )
                    }
                    original = _state.value
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(guardando = false, mensaje = "Error: ${e.message}")
                    }
                }
        }
    }
}