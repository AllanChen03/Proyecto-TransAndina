package com.example.proyecto1ap.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoFechaSelector
import com.example.proyecto1ap.ui.componentes.CampoSelector
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.MensajeAdvertencia
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

private const val TODOS = "Todos"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaReportes(
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: Reporte = viewModel(key = remember { UUID.randomUUID().toString() })
) {
    val s by vm.state.collectAsState()
    val formato = remember {
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-CR"))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Reportes de mantenimiento") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaSeccion(titulo = "Filtros") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoSelector(
                        etiqueta = "Vehículo",
                        valorSeleccionado = s.vehiculos
                            .find { it.id == s.filtros.vehiculoId }?.placa ?: TODOS,
                        opciones = listOf(TODOS) + s.vehiculos.map { it.placa },
                        onSeleccion = { placa ->
                            vm.onVehiculo(s.vehiculos.find { it.placa == placa }?.id)
                        }
                    )

                    CampoSelector(
                        etiqueta = "Tipo de mantenimiento",
                        valorSeleccionado = when (s.filtros.tipo) {
                            "PREVENTIVO" -> "Preventivo"
                            "CORRECTIVO" -> "Correctivo"
                            else -> TODOS
                        },
                        opciones = listOf(TODOS, "Preventivo", "Correctivo"),
                        onSeleccion = { etiqueta ->
                            vm.onTipo(if (etiqueta == TODOS) null else etiqueta.uppercase())
                        }
                    )

                    CampoFechaSelector(
                        etiqueta = "Fecha inicial",
                        valor = s.filtros.desde,
                        onFechaSeleccionada = vm::onDesde
                    )
                    CampoFechaSelector(
                        etiqueta = "Fecha final",
                        valor = s.filtros.hasta,
                        onFechaSeleccionada = vm::onHasta
                    )

                    CampoTexto(
                        etiqueta = "Costo mínimo (₡)",
                        valor = s.filtros.costoMin,
                        onValorChange = vm::onCostoMin,
                        placeholder = "Ej: 20000",
                        tipoTeclado = KeyboardType.Number
                    )
                    CampoTexto(
                        etiqueta = "Costo máximo (₡)",
                        valor = s.filtros.costoMax,
                        onValorChange = vm::onCostoMax,
                        placeholder = "Ej: 100000",
                        tipoTeclado = KeyboardType.Number
                    )

                    s.aviso?.let { MensajeAdvertencia(it) }

                    BotonPrimario(
                        texto = if (s.cargando) "Consultando..." else "Consultar",
                        onClick = vm::consultar,
                        habilitado = !s.cargando
                    )
                    TextButton(
                        onClick = vm::limpiar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Limpiar filtros", color = AzulPrimario)
                    }
                }
            }

            when {
                s.cargando -> Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                s.error != null -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.error ?: "", color = RojoTexto, fontSize = 14.sp)
                    TextButton(onClick = vm::consultar) { Text("Reintentar") }
                }

                else -> {
                    TarjetaSeccion(titulo = "Resumen") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Servicios", fontSize = 12.sp, color = TextoSecundario)
                                Text(
                                    "${s.resultados.size}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoPrincipal
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Costo total", fontSize = 12.sp, color = TextoSecundario)
                                Text(
                                    "₡ %,.0f".format(s.totalCosto),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPrimario
                                )
                            }
                        }
                    }

                    TarjetaSeccion(titulo = "Resultados") {
                        if (s.resultados.isEmpty()) {
                            Text(
                                "No hay mantenimientos que coincidan con los filtros.",
                                fontSize = 13.sp,
                                color = TextoSecundario
                            )
                        }
                        s.resultados.forEachIndexed { i, m ->
                            if (i > 0) {
                                HorizontalDivider(
                                    color = Borde,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                            FilaMantenimiento(m, formato)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilaMantenimiento(m: MantenimientoReporte, formato: DateTimeFormatter) {
    val fecha = runCatching { LocalDate.parse(m.fecha).format(formato) }.getOrDefault(m.fecha)
    val preventivo = m.tipoMantenimiento == "PREVENTIVO"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(fecha, fontSize = 13.sp, color = TextoSecundario)
            ChipEstado(
                texto = if (preventivo) "PREVENTIVO" else "CORRECTIVO",
                estado = if (preventivo) EstadoVisual.NEUTRO else EstadoVisual.CRITICO
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "${m.vehiculos?.placa ?: "—"} • ${m.descripcion}",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoPrincipal
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Mecánico: ${m.usuarios?.nombreCompleto ?: "—"}",
                fontSize = 13.sp,
                color = TextoSecundario
            )
            Text(
                m.costo?.let { "₡ %,.0f".format(it) } ?: "Sin costo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )
        }
    }
}
