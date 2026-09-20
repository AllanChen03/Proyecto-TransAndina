package com.example.proyecto1ap.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CentroAlertasState(
    val alertas: List<AlertaListado> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
) {
    val agrupadas: Map<String, List<AlertaListado>>
        get() = alertas.groupBy { it.tipo }
}

class CentroAlertas : ViewModel() {

    private val repo = AlertaRepository()

    private val _state = MutableStateFlow(CentroAlertasState())
    val state = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            val uid = SupabaseManager.client.auth.currentUserOrNull()?.id
            if (uid == null) {
                _state.update { it.copy(cargando = false, error = "Sesión no válida") }
                return@launch
            }
            _state.update { it.copy(cargando = true, error = null) }
            repo.listarParaMecanico(uid)
                .onSuccess { lista -> _state.update { it.copy(alertas = lista, cargando = false) } }
                .onFailure { e -> _state.update { it.copy(cargando = false, error = "No se pudo cargar: ${e.message}") } }
        }
    }
}