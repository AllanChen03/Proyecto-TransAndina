package com.example.proyecto1ap.alertas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.proyecto1ap.ui.theme.AmarilloTexto
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoFondo
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import com.example.proyecto1ap.ui.theme.VerdeFondo
import com.example.proyecto1ap.ui.theme.VerdeTexto
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCentroAlertas(
    conductorId: String?,
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: CentroAlertas = viewModel(
        key = remember { UUID.randomUUID().toString() },
        factory = CentroAlertasFactory(conductorId)
    )
) {
    val s by vm.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Centro de alertas") },
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

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResumenAlertas(s.alertas.size)

                    val mantenimiento = s.alertas.filter { it.categoria == CategoriaAlerta.MANTENIMIENTO }
                    val documentos = s.alertas.filter { it.categoria == CategoriaAlerta.DOCUMENTO }

                    if (mantenimiento.isNotEmpty()) {
                        TituloSeccion("MANTENIMIENTO PREVENTIVO")
                        mantenimiento.forEach { TarjetaAlerta(it) }
                    }
                    if (documentos.isNotEmpty()) {
                        TituloSeccion("DOCUMENTOS POR VENCER")
                        documentos.forEach { TarjetaAlerta(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenAlertas(cantidad: Int) {
    val hayAlertas = cantidad > 0
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (hayAlertas) RojoFondo else VerdeFondo,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = if (hayAlertas) "Tienes $cantidad alertas activas" else "Sin alertas activas",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (hayAlertas) RojoTexto else VerdeTexto
        )
        Text(
            text = if (hayAlertas) "Requieren revisión por parte de los conductores o mecánicos."
            else "Todos los vehículos están al día.",
            fontSize = 13.sp,
            color = if (hayAlertas) RojoTexto else VerdeTexto
        )
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextoSecundario,
        modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun TarjetaAlerta(alerta: AlertaCalculada) {
    val color = if (alerta.severidad == Severidad.URGENTE) RojoTexto else AmarilloTexto
    val etiqueta = if (alerta.severidad == Severidad.URGENTE) "URGENTE" else "ADVERTENCIA"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(color)
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        alerta.placa,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Text(etiqueta, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                }
                Text(
                    alerta.titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal
                )
                Text(alerta.detalle, fontSize = 13.sp, color = TextoSecundario)
            }
        }
    }
}