package com.example.proyecto1ap.vehiculo

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.ChipEstado
import com.example.proyecto1ap.ui.componentes.EstadoVisual
import com.example.proyecto1ap.ui.componentes.FilaDato
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.AzulClaro
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleVehiculo(
    vehiculoId: Long,
    onVolver: () -> Unit = {},
    onEditar: (Long) -> Unit = {},
    onHistorialKilometraje: (Long) -> Unit = {},
    onHistorialMantenimientos: (Long) -> Unit = {},
    mostrarEditar: Boolean = true,
    modifier: Modifier = Modifier,
    vm: DetalleVehiculo = viewModel(
        key = vehiculoId.toString(),
        factory = DetalleVehiculoFactory(vehiculoId)
    )
) {
    val s by vm.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalles de vehículo", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (mostrarEditar) {
                        IconButton(onClick = { onEditar(vehiculoId) }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar",
                                tint = AzulPrimario
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->

        val v = s.vehiculo

        when {
            s.cargando -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            s.error != null || v == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(s.error ?: "Vehículo no encontrado", color = RojoTexto)
            }

            else -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(FondoApp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ---------- Datos generales ----------
                TarjetaSeccion {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = v.placa,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        ChipEstado(
                            texto = if (v.estado == "ACTIVO") "ACTIVO" else "INACTIVO",
                            estado = if (v.estado == "ACTIVO") EstadoVisual.NEUTRO
                            else EstadoVisual.CRITICO
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Borde)
                    Spacer(Modifier.height(6.dp))

                    FilaDato("Marca", v.marca)
                    FilaDato("Modelo", v.modelo)
                    FilaDato("Año", v.anio.toString())
                    FilaDato("Color", v.color)
                    FilaDato("Tipo", etiquetaDe(TIPOS_VEHICULO, v.tipoVehiculo))
                    FilaDato("Combustible", etiquetaDe(COMBUSTIBLES, v.tipoCombustible))
                    if (v.capacidad != null) {
                        FilaDato("Capacidad", v.capacidad.toString())
                    }
                }

                // ---------- Kilometraje ----------
                TarjetaSeccion(titulo = "Kilometraje actual") {
                    if (v.kilometrajeActual != null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "%,d".format(v.kilometrajeActual),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                text = "km",
                                fontSize = 16.sp,
                                color = TextoSecundario,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        if (v.fechaUltimoKm != null) {
                            Text(
                                text = "Última actualización: ${v.fechaUltimoKm}",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                    } else {
                        Text(
                            text = "Sin registros de kilometraje",
                            fontSize = 15.sp,
                            color = TextoSecundario
                        )
                    }
                }

                // ---------- Conductor ----------
                TarjetaSeccion(titulo = "Conductor asignado") {
                    if (v.conductorNombre != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AzulClaro),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = v.conductorNombre
                                        .split(" ")
                                        .filter { it.isNotBlank() }
                                        .take(2)
                                        .joinToString("") { it.first().uppercase() },
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPrimario
                                )
                            }
                            Spacer(Modifier.size(12.dp))
                            Column {
                                Text(
                                    text = v.conductorNombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (v.conductorLicencia != null) {
                                    Text(
                                        text = "Licencia: ${v.conductorLicencia}",
                                        fontSize = 13.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Sin conductor asignado",
                            fontSize = 15.sp,
                            color = TextoSecundario
                        )
                    }
                }

                // ---------- Documentación legal ----------
                TarjetaSeccion(titulo = "Documentación legal") {
                    FilaDocumento("Marchamo", v.vencimientoMarchamo)
                    Spacer(Modifier.height(10.dp))
                    FilaDocumento("Revisión técnica", v.vencimientoRevisionTecnica)
                    Spacer(Modifier.height(10.dp))
                    FilaDocumento("Seguro", v.vencimientoSeguro)
                }

                // ---------- Mantenimientos ----------
                TarjetaSeccion(titulo = "Mantenimientos") {
                    FilaDato("Total registrados", v.totalMantenimientos.toString())
                    FilaDato(
                        "Costo acumulado",
                        "₡%,.0f".format(v.costoTotalMantenimientos)
                    )
                    if (v.fechaUltimoMantenimiento != null) {
                        FilaDato("Último servicio", v.fechaUltimoMantenimiento)
                        if (v.categoriaUltimoMantenimiento != null) {
                            FilaDato(
                                "Categoría",
                                v.categoriaUltimoMantenimiento
                                    .replace("_", " ")
                                    .lowercase()
                                    .replaceFirstChar { it.uppercase() }
                            )
                        }
                    }
                }

                // ---------- Botones de historial ----------
                Spacer(Modifier.height(8.dp))

                BotonPrimario(
                    texto = "Historial de kilometraje",
                    onClick = { onHistorialKilometraje(v.id) }
                )

                OutlinedButton(
                    onClick = { onHistorialMantenimientos(v.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AzulPrimario),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = AzulPrimario
                    )
                ) {
                    Text(
                        text = "Historial de mantenimientos",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FilaDocumento(nombre: String, fecha: String?) {
    val vigencia = evaluarVigencia(fecha)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoPrincipal
            )
            Text(
                text = vigencia.textoFecha,
                fontSize = 12.sp,
                color = TextoSecundario
            )
        }
        ChipEstado(texto = vigencia.etiqueta, estado = vigencia.estado)
    }
}

private fun etiquetaDe(lista: List<Pair<String, String>>, valor: String): String =
    lista.find { it.first == valor }?.second ?: valor
