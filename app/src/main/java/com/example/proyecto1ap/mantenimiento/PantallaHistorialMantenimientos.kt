package com.example.proyecto1ap.mantenimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.DialogoRangoFechas
import com.example.proyecto1ap.ui.componentes.TarjetaRegistroMantenimiento
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.TextoSecundario

@Composable
private fun DialogoTipo(
    seleccionado: TipoMantenimiento?,
    onSeleccionar: (TipoMantenimiento?) -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Filtrar por tipo") },
        text = {
            Column {
                listOf(
                    null to "Todos",
                    TipoMantenimiento.PREVENTIVO to "Preventivo",
                    TipoMantenimiento.CORRECTIVO to "Correctivo"
                ).forEach { (tipo, etiqueta) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = seleccionado == tipo,
                            onClick = { onSeleccionar(tipo) }
                        )
                        Text(etiqueta)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialMantenimientos(
    vehiculoId: Long? = null,
    soloMios: Boolean = true,
    onVolver: () -> Unit = {},
    onMantenimiento: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    vm: HistorialMantenimientos = viewModel(
        key = "hist_${vehiculoId ?: "todos"}_$soloMios",
        factory = HistorialMantenimientosFactory(vehiculoId, soloMios)
    )
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    var mostrarDialogoTipo by remember { mutableStateOf(false) }
    var mostrarDialogoRango by remember { mutableStateOf(false) }

    LaunchedEffect(s.error) {
        s.error?.let { snackbar.showSnackbar(it) }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Historial de mantenimientos", fontSize = 18.sp)
                        val subtitulo = s.placaVehiculo
                            ?: if (s.cargando) null
                            else "${s.registrosFiltrados.size} registros"
                        if (subtitulo != null) {
                            Text(subtitulo, fontSize = 13.sp, color = TextoSecundario)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = s.filtroTipo != null,
                    onClick = { mostrarDialogoTipo = true },
                    label = {
                        Text(
                            if (s.filtroTipo == null) "Tipo: Todos"
                            else "Tipo: ${s.filtroTipo!!.etiqueta}",
                            fontSize = 13.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AzulPrimario,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = s.desde.isNotBlank() || s.hasta.isNotBlank(),
                    onClick = { mostrarDialogoRango = true },
                    label = {
                        Text(
                            if (s.desde.isBlank() && s.hasta.isBlank()) "Rango de fechas"
                            else "${s.desde} a ${s.hasta}",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AzulPrimario,
                        selectedLabelColor = Color.White
                    )
                )
            }

            if (mostrarDialogoTipo) {
                DialogoTipo(
                    seleccionado = s.filtroTipo,
                    onSeleccionar = { tipo ->
                        if (tipo == null) vm.limpiarFiltroTipo() else vm.onFiltroTipo(tipo)
                        mostrarDialogoTipo = false
                    },
                    onCerrar = { mostrarDialogoTipo = false }
                )
            }

            if (mostrarDialogoRango) {
                DialogoRangoFechas(
                    abierto = true,
                    desde = s.desde,
                    hasta = s.hasta,
                    onCambioDesde = { desde -> vm.onRango(desde, s.hasta) },
                    onCambioHasta = { hasta -> vm.onRango(s.desde, hasta) },
                    onLimpiar = { vm.limpiarRango() },
                    onCerrar = { mostrarDialogoRango = false }
                )
            }

            when {
                s.cargando -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                s.registrosFiltrados.isEmpty() -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay mantenimientos registrados con estos filtros",
                        color = TextoSecundario,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.registrosFiltrados, key = { it.id }) { registro ->
                        TarjetaRegistroMantenimiento(
                            registro = registro,
                            onClick = { onMantenimiento(registro.id) }
                        )
                    }
                }
            }
        }
    }
}