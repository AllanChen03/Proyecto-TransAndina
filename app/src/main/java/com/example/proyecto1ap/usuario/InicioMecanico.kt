package com.example.proyecto1ap.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InicioMecanicoState(
    val ultimos: List<MantenimientoListado> = emptyList(),
    val totalMantenimientos: Int = 0,
    val calificacion: Float = 0f,
    val alertasActivas: Int = 0,
    val cargando: Boolean = true,
    val error: String? = null
)

class InicioMecanico : ViewModel() {

    private val repo = MantenimientoRepository()

    private val _state = MutableStateFlow(InicioMecanicoState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }

            val uid = SupabaseManager.client.auth.currentUserOrNull()?.id
            if (uid == null) {
                _state.update { it.copy(cargando = false, error = "Sesión no válida") }
                return@launch
            }

            repo.listarPorMecanico(uid)
                .onSuccess { lista ->
                    _state.update {
                        it.copy(
                            ultimos = lista.take(4),
                            totalMantenimientos = lista.size,
                            calificacion = 0f,
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
}