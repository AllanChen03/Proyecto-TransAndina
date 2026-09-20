package com.example.proyecto1ap.vehiculo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyecto1ap.usuario.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditarVehiculoState(
    val id: Long = 0,
    val placa: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: String = "",
    val color: String = "",
    val tipoVehiculo: String? = null,
    val tipoCombustible: String? = null,
    val capacidad: String = "",
    val conductorId: String? = null,
    val estado: String = "ACTIVO",
    val vencimientoMarchamo: String = "",
    val vencimientoRevisionTecnica: String = "",
    val vencimientoSeguro: String = "",
    val conductores: List<Usuario> = emptyList(),
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensaje: String? = null,
    val guardadoExitoso: Boolean = false
) {
    val puedeGuardar: Boolean
        get() = placa.isNotBlank() &&
                marca.isNotBlank() &&
                modelo.isNotBlank() &&
                (anio.toIntOrNull() ?: 0) in 1900..2100 &&
                color.isNotBlank() &&
                tipoVehiculo != null &&
                tipoCombustible != null &&
                !guardando && !cargando
}

class EditarVehiculo(private val vehiculoId: Long) : ViewModel() {

    private val repo = VehiculoRepository()

    private val _state = MutableStateFlow(EditarVehiculoState())
    val state = _state.asStateFlow()

    private var original: EditarVehiculoState? = null

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            val conductores = repo.conductoresActivos().getOrDefault(emptyList())

            repo.obtenerPorId(vehiculoId)
                .onSuccess { v ->
                    _state.update {
                        it.copy(
                            id = v.id,
                            placa = v.placa,
                            marca = v.marca,
                            modelo = v.modelo,
                            anio = v.anio.toString(),
                            color = v.color,
                            tipoVehiculo = v.tipoVehiculo,
                            tipoCombustible = v.tipoCombustible,
                            capacidad = v.capacidad?.toString() ?: "",
                            conductorId = v.conductorId,
                            estado = v.estado,
                            vencimientoMarchamo = v.vencimientoMarchamo ?: "",
                            vencimientoRevisionTecnica = v.vencimientoRevisionTecnica ?: "",
                            vencimientoSeguro = v.vencimientoSeguro ?: "",
                            conductores = conductores,
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

    fun onPlaca(v: String) = _state.update { it.copy(placa = v.uppercase()) }
    fun onMarca(v: String) = _state.update { it.copy(marca = v) }
    fun onModelo(v: String) = _state.update { it.copy(modelo = v) }
    fun onAnio(v: String) = _state.update { it.copy(anio = v.filter(Char::isDigit).take(4)) }
    fun onColor(v: String) = _state.update { it.copy(color = v) }
    fun onCapacidad(v: String) = _state.update { it.copy(capacidad = v.filter(Char::isDigit)) }
    fun onTipoVehiculo(v: String) = _state.update { it.copy(tipoVehiculo = v) }
    fun onTipoCombustible(v: String) = _state.update { it.copy(tipoCombustible = v) }
    fun onConductor(id: String?) = _state.update { it.copy(conductorId = id) }
    fun onEstado(v: String) = _state.update { it.copy(estado = v) }
    fun onVenceMarchamo(v: String) = _state.update { it.copy(vencimientoMarchamo = v) }
    fun onVenceRevision(v: String) = _state.update { it.copy(vencimientoRevisionTecnica = v) }
    fun onVenceSeguro(v: String) = _state.update { it.copy(vencimientoSeguro = v) }
    fun limpiarMensaje() = _state.update { it.copy(mensaje = null) }

    fun hayCambios(): Boolean {
        val o = original ?: return false
        val s = _state.value
        return o.placa != s.placa ||
                o.marca != s.marca ||
                o.modelo != s.modelo ||
                o.anio != s.anio ||
                o.color != s.color ||
                o.tipoVehiculo != s.tipoVehiculo ||
                o.tipoCombustible != s.tipoCombustible ||
                o.capacidad != s.capacidad ||
                o.conductorId != s.conductorId ||
                o.estado != s.estado ||
                o.vencimientoMarchamo != s.vencimientoMarchamo ||
                o.vencimientoRevisionTecnica != s.vencimientoRevisionTecnica ||
                o.vencimientoSeguro != s.vencimientoSeguro
    }

    fun guardar() {
        val s = _state.value
        if (!s.puedeGuardar) return

        viewModelScope.launch {
            _state.update { it.copy(guardando = true, mensaje = null) }

            val datos = VehiculoEditable(
                placa = s.placa.trim(),
                marca = s.marca.trim(),
                modelo = s.modelo.trim(),
                anio = s.anio.toInt(),
                color = s.color.trim(),
                tipoVehiculo = s.tipoVehiculo!!,
                tipoCombustible = s.tipoCombustible!!,
                capacidad = s.capacidad.toIntOrNull(),
                conductorId = s.conductorId,
                estado = s.estado,
                vencimientoMarchamo = s.vencimientoMarchamo.ifBlank { null },
                vencimientoRevisionTecnica = s.vencimientoRevisionTecnica.ifBlank { null },
                vencimientoSeguro = s.vencimientoSeguro.ifBlank { null }
            )

            repo.actualizar(s.id, datos)
                .onSuccess {
                    _state.update {
                        it.copy(
                            guardando = false,
                            mensaje = "Cambios guardados",
                            guardadoExitoso = true
                        )
                    }
                    original = _state.value
                }
                .onFailure { e ->
                    val msg = when {
                        e.message?.contains("duplicate key") == true ->
                            "Ya existe otro vehículo con esa placa"
                        e.message?.contains("row-level security") == true ->
                            "No tenés permisos para editar vehículos"
                        else -> "Error: ${e.message}"
                    }
                    _state.update { it.copy(guardando = false, mensaje = msg) }
                }
        }
    }
}

class EditarVehiculoFactory(private val vehiculoId: Long) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditarVehiculo(vehiculoId) as T
    }
}