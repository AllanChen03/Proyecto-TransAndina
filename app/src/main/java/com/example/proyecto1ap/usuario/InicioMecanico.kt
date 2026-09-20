package com.example.proyecto1ap.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.mantenimiento.AlertaRepository
import com.example.proyecto1ap.mantenimiento.MantenimientoRepository
import com.example.proyecto1ap.mantenimiento.ResumenVehiculo
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InicioMecanicoState(
    val vehiculosEnTaller: List<ResumenVehiculo> = emptyList(),
    val vehiculosPendientes: List<ResumenVehiculo> = emptyList(),
    val totalMantenimientos: Int = 0,
    val calificacion: Float = 0f,
    val alertasActivas: Int = 0,
    val cargando: Boolean = true,
    val error: String? = null
)

class InicioMecanico : ViewModel() {

    private val repo = MantenimientoRepository()
    private val alertaRepo = AlertaRepository()

    private val _state = MutableStateFlow(InicioMecanicoState())
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

            val taller = repo.vehiculosEnTaller().getOrDefault(emptyList())
            val pendientes = repo.vehiculosPendientes().getOrDefault(emptyList())
            val total = repo.contarPorMecanico(uid).getOrDefault(0)
            val alertas = alertaRepo.contarPara(uid)
            val calculadas = repo.calcularCalificacion()

            _state.update {
                it.copy(
                    vehiculosEnTaller = taller,
                    vehiculosPendientes = pendientes,
                    totalMantenimientos = total,
                    alertasActivas = alertas,
                    calificacion = calculadas,
                    cargando = false
                )
            }
        }
    }
}