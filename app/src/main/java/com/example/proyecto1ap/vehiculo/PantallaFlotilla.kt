package com.example.proyecto1ap.vehiculo

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
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import androidx.compose.runtime.LaunchedEffect

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

    LaunchedEffect(Unit) { vm.cargar() }
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
    val (textoMantenimiento, estadoMantenimiento) = estadoMantenimiento(vehiculo)
    val (textoDocumentos, estadoDocumentos) = estadoGeneral(vehiculo)

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vehiculo.placa,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Spacer(Modifier.width(8.dp))
                        val activo = vehiculo.estado == "ACTIVO"
                        ChipEstado(
                            texto = if (activo) "Activo" else "Inactivo",
                            estado = if (activo) EstadoVisual.OK else EstadoVisual.CRITICO
                        )
                    }
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

            IndicadorSemaforo(textoMantenimiento, colorDe(estadoMantenimiento))

            Spacer(Modifier.height(6.dp))

            IndicadorSemaforo(textoDocumentos, colorDe(estadoDocumentos))
        }
    }
}

@Composable
private fun IndicadorSemaforo(texto: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            color = color
        )
    }
}