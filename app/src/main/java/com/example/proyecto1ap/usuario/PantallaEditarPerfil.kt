package com.example.proyecto1ap.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.ui.componentes.FilaDato
import com.example.proyecto1ap.ui.componentes.TarjetaSeccion
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.TextoSecundario
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun PantallaEditarPerfil(
    usuario: Usuario,
    onPerfilActualizado: (Usuario) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var nombreCompleto by remember(usuario.id) { mutableStateOf(usuario.nombreCompleto) }
    var telefono by remember(usuario.id) { mutableStateOf(usuario.telefono.orEmpty()) }
    var mensaje by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoApp)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar perfil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Nombre completo")
        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nuevoTexto ->
                nombreCompleto = soloLetrasEspacios(nuevoTexto)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Teléfono")
        OutlinedTextField(
            value = telefono,
            onValueChange = { nuevoTexto ->
                telefono = soloNumeros(nuevoTexto, LARGO_TELEFONO_USUARIO)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(8.dp))

        TarjetaSeccion(titulo = "Datos que no se pueden modificar") {
            FilaDato("Cédula", usuario.cedula)
            FilaDato("Correo", usuario.correo)
            if (usuario.rol == "CONDUCTOR") {
                FilaDato(
                    "Licencia",
                    usuario.numeroLicencia?.ifBlank { "Sin registrar" } ?: "Sin registrar"
                )
            }
            FilaDato(
                "Rol",
                when (usuario.rol) {
                    "CONDUCTOR" -> "Conductor"
                    "MECANICO" -> "Mecánico"
                    else -> "Encargado de flota"
                }
            )
        }

        Text(
            text = "Para corregir estos datos, contactá al encargado de flota",
            fontSize = 12.sp,
            color = TextoSecundario
        )

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = if (mensaje.startsWith("Error")) RojoTexto else AzulPrimario
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !guardando && nombreCompleto.isNotBlank(),
            onClick = {
                scope.launch {
                    if (nombreCompleto.isBlank()) {
                        mensaje = "Debe ingresar el nombre completo"
                        return@launch
                    }
                    if (telefono.isBlank()) {
                        mensaje = "Debe ingresar el teléfono"
                        return@launch
                    }

                    guardando = true
                    try {
                        SupabaseManager.client.from("usuarios").update(
                            UsuarioPerfilActualizacion(
                                nombreCompleto = nombreCompleto.trim(),
                                telefono = telefono.trim()
                            )
                        ) {
                            filter { eq("id", usuario.id) }
                        }

                        val actualizado = usuario.copy(
                            nombreCompleto = nombreCompleto.trim(),
                            telefono = telefono.trim()
                        )

                        mensaje = "Perfil actualizado"
                        onPerfilActualizado(actualizado)
                    } catch (e: Exception) {
                        mensaje = "Error al actualizar: ${e.message}"
                    } finally {
                        guardando = false
                    }
                }
            }
        ) {
            Text(if (guardando) "Guardando..." else "Guardar cambios")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onVolver
        ) {
            Text("Volver")
        }
    }
}