package com.example.proyecto1ap.Vehiculo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.theme.AmarilloTexto
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.ui.theme.VerdeTexto
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFlotilla(
    onVolver: () -> Unit = {},
    onAgregar: () -> Unit = {},
    onVehiculo: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    vm: ListaVehiculos = viewModel()
) {
    val s by vm.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Gestión de flotilla",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!s.cargando) {
                            Text(
                                "${s.vehiculos.size} vehículos",
                                fontSize = 13.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = AzulPrimario,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar vehículo")
            }
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

                s.vehiculos.isEmpty() -> Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No hay vehículos registrados",
                        fontSize = 16.sp,
                        color = TextoSecundario
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tocá el botón + para agregar el primero",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.vehiculos, key = { it.id }) { vehiculo ->
                        TarjetaVehiculo(
                            vehiculo = vehiculo,
                            onClick = { onVehiculo(vehiculo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaVehiculo(
    vehiculo: VehiculoListado,
    onClick: () -> Unit
) {
    val estado = calcularEstado(vehiculo)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Superficie),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehiculo.placa,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.anio}",
                        fontSize = 14.sp,
                        color = TextoSecundario
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = vehiculo.conductorNombre ?: "Sin conductor asignado",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextoSecundario
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Borde)
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(estado.second)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = estado.first,
                        fontSize = 13.sp,
                        color = estado.second
                    )
                }

                Text(
                    text = vehiculo.kilometrajeActual
                        ?.let { "%,d km".format(it) }
                        ?: "Sin km",
                    fontSize = 13.sp,
                    color = TextoSecundario
                )
            }
        }
    }
}

private fun calcularEstado(v: VehiculoListado): Pair<String, Color> {
    if (v.estado == "INACTIVO") {
        return "Fuera de servicio" to TextoSecundario
    }

    val fechas = listOfNotNull(
        v.vencimientoMarchamo,
        v.vencimientoRevisionTecnica,
        v.vencimientoSeguro
    ).mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }

    if (fechas.isEmpty()) {
        return "Sin documentos" to TextoSecundario
    }

    val hoy = LocalDate.now()
    val vencidos = fechas.count { it.isBefore(hoy) }
    if (vencidos > 0) {
        return "Documentos vencidos ($vencidos)" to RojoTexto
    }

    val dias = ChronoUnit.DAYS.between(hoy, fechas.min())
    if (dias <= 30) {
        return "Vence en $dias días" to AmarilloTexto
    }

    return "Al día" to VerdeTexto
}