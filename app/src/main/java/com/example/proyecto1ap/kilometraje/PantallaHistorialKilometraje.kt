package com.example.proyecto1ap.kilometraje

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialKilometraje(
    vehiculoId: Long?,
    conductorId: String?,
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: HistorialKilometraje = viewModel(
        key = remember { UUID.randomUUID().toString() },
        factory = HistorialKilometrajeFactory(vehiculoId, conductorId)
    )
) {
    val s by vm.state.collectAsState()
    val formato = remember {
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-CR"))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Historial de kilometraje") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
        ) {
            when {
                s.cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                s.error != null -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.error ?: "", color = RojoTexto, fontSize = 14.sp)
                    TextButton(onClick = vm::cargar) { Text("Reintentar") }
                }

                else -> {
                    val vehiculo = s.vehiculo
                    val kmTotal = vehiculo?.kilometrajeActual ?: s.registros.firstOrNull()?.kilometraje
                    val puntos = remember(s.registros) {
                        s.registros
                            .sortedWith(compareBy({ LocalDate.parse(it.fecha) }, { it.id }))
                            .map { LocalDate.parse(it.fecha) to it.kilometraje }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (vehiculo != null) {
                            TarjetaSeccion {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        vehiculo.placa,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoPrincipal
                                    )
                                    Text(
                                        "${vehiculo.marca} ${vehiculo.modelo}",
                                        fontSize = 14.sp,
                                        color = TextoSecundario
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                Text("Kilometraje total", fontSize = 13.sp, color = TextoSecundario)
                                Text(
                                    text = kmTotal?.let { "%,d km".format(it) } ?: "Sin registros",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoPrincipal
                                )
                            }
                        }

                        TarjetaSeccion(titulo = "Evolución del kilometraje") {
                            if (puntos.size < 2) {
                                Text(
                                    "Se necesitan al menos 2 registros para mostrar la gráfica.",
                                    fontSize = 13.sp,
                                    color = TextoSecundario
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Máx: %,d km".format(puntos.maxOf { it.second }),
                                        fontSize = 12.sp,
                                        color = TextoSecundario
                                    )
                                    Text(
                                        "Mín: %,d km".format(puntos.minOf { it.second }),
                                        fontSize = 12.sp,
                                        color = TextoSecundario
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                GraficaKilometraje(puntos)
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        puntos.first().first.format(formato),
                                        fontSize = 12.sp,
                                        color = TextoSecundario
                                    )
                                    Text(
                                        puntos.last().first.format(formato),
                                        fontSize = 12.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }

                        TarjetaSeccion(titulo = "Registros anteriores") {
                            if (s.registros.isEmpty()) {
                                Text(
                                    "Aún no hay registros de kilometraje.",
                                    fontSize = 13.sp,
                                    color = TextoSecundario
                                )
                            }
                            s.registros.forEachIndexed { i, r ->
                                if (i > 0) {
                                    HorizontalDivider(
                                        color = Borde,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "%,d km".format(r.kilometraje),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoPrincipal
                                        )
                                        Text(
                                            "Registrado por: ${r.usuarios?.nombreCompleto ?: "—"}",
                                            fontSize = 13.sp,
                                            color = TextoSecundario
                                        )
                                    }
                                    Text(
                                        LocalDate.parse(r.fecha).format(formato),
                                        fontSize = 13.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}