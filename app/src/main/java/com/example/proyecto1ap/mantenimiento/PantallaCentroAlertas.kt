package com.example.proyecto1ap.mantenimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto1ap.ui.componentes.BannerAlertas
import com.example.proyecto1ap.ui.componentes.EncabezadoGrupoAlerta
import com.example.proyecto1ap.ui.componentes.TarjetaAlerta
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCentroAlertas(
    onVolver: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: CentroAlertas = viewModel()
) {
    val s by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.error) {
        s.error?.let {
            snackbar.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Centro de alertas") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
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
            if (s.cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (s.alertas.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin alertas activas",
                        color = TextoSecundario,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        BannerAlertas(total = s.alertas.size)
                        Spacer(Modifier.height(4.dp))
                    }
                    s.agrupadas.forEach { (tipo, alertasDelTipo) ->
                        item(key = "grupo_$tipo") {
                            EncabezadoGrupoAlerta(
                                nombreGrupo = tipo,
                                cantidad = alertasDelTipo.size
                            )
                        }
                        items(alertasDelTipo, key = { it.id }) { alerta ->
                            TarjetaAlerta(alerta = alerta)
                        }
                    }
                }
            }
        }
    }
}