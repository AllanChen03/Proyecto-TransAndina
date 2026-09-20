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
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
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
    var cedula by remember(usuario.id) { mutableStateOf(usuario.cedula) }
    var telefono by remember(usuario.id) { mutableStateOf(usuario.telefono.orEmpty()) }
    var licenciaNum by remember(usuario.id) { mutableStateOf(usuario.numeroLicencia.orEmpty()) }
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

        Text("Cédula")
        OutlinedTextField(
            value = cedula,
            onValueChange = { nuevoTexto ->
                cedula = soloNumeros(nuevoTexto, LARGO_CEDULA_USUARIO)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Text("Correo electrónico")
        OutlinedTextField(
            value = usuario.correo,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
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

        if (usuario.rol == "CONDUCTOR") {
            Text("Número de licencia")
            OutlinedTextField(
                value = licenciaNum,
                onValueChange = { nuevoTexto -> licenciaNum = nuevoTexto },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = if (mensaje.startsWith("Error")) RojoTexto else AzulPrimario
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !guardando,
            onClick = {
                scope.launch {
                    val errorDatos = validarDatosUsuario(
                        nombreCompleto = nombreCompleto,
                        cedula = cedula,
                        correo = usuario.correo,
                        telefono = telefono,
                        rol = usuario.rol,
                        numeroLicencia = licenciaNum
                    )

                    if (errorDatos != null) {
                        mensaje = errorDatos
                        return@launch
                    }

                    guardando = true
                    try {
                        val licenciaFinal = if (usuario.rol == "CONDUCTOR") {
                            licenciaNum.trim()
                        } else {
                            null
                        }

                        SupabaseManager.client.from("usuarios").update(
                            UsuarioPerfilActualizacion(
                                nombreCompleto = nombreCompleto.trim(),
                                cedula = cedula.trim(),
                                telefono = telefono.trim(),
                                numeroLicencia = licenciaFinal
                            )
                        ) {
                            filter { eq("id", usuario.id) }
                        }

                        val actualizado = usuario.copy(
                            nombreCompleto = nombreCompleto.trim(),
                            cedula = cedula.trim(),
                            telefono = telefono.trim(),
                            numeroLicencia = licenciaFinal
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
