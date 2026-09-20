package com.example.proyecto1ap.usuario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto1ap.SupabaseManager
import com.example.proyecto1ap.ui.componentes.BotonPrimario
import com.example.proyecto1ap.ui.componentes.CampoTexto
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import com.example.proyecto1ap.ui.theme.FondoApp
import com.example.proyecto1ap.ui.theme.RojoTexto
import com.example.proyecto1ap.ui.theme.Superficie
import com.example.proyecto1ap.ui.theme.TextoPrincipal
import com.example.proyecto1ap.ui.theme.TextoSecundario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun PantallaInicial(
    modifier: Modifier = Modifier,
    irAHomePorRol: (Usuario) -> Unit,
    irARecuperarCorreo: () -> Unit,
    irARegistro: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var verContrasena by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FondoApp)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(AzulPrimario),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TA",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "TransAndina",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            Text(
                text = "Gestión de mantenimiento de flotilla",
                fontSize = 14.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Superficie),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    CampoTexto(
                        etiqueta = "Correo electrónico",
                        valor = correo,
                        onValorChange = { correo = it },
                        placeholder = "nombre@correo.com",
                        tipoTeclado = KeyboardType.Email
                    )

                    Column {
                        Text(
                            text = "Contraseña",
                            fontSize = 13.sp,
                            color = TextoSecundario,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = contrasena,
                            onValueChange = { contrasena = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("••••••••", color = TextoSecundario)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            visualTransformation = if (verContrasena)
                                VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { verContrasena = !verContrasena }) {
                                    Icon(
                                        imageVector = if (verContrasena)
                                            Icons.Filled.VisibilityOff
                                        else Icons.Filled.Visibility,
                                        contentDescription = if (verContrasena)
                                            "Ocultar contraseña"
                                        else "Mostrar contraseña",
                                        tint = TextoSecundario
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AzulPrimario,
                                unfocusedBorderColor = Borde,
                                focusedContainerColor = Superficie,
                                unfocusedContainerColor = Superficie
                            )
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "¿Olvidó su contraseña?",
                            fontSize = 13.sp,
                            color = AzulPrimario,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clickable { irARecuperarCorreo() }
                        )
                    }

                    if (mensaje.isNotBlank()) {
                        Text(
                            text = mensaje,
                            fontSize = 13.sp,
                            color = RojoTexto,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    BotonPrimario(
                        texto = if (cargando) "Ingresando..." else "Ingresar",
                        onClick = {
                            scope.launch {
                                mensaje = ""

                                if (correo.isBlank()) {
                                    mensaje = "Debe ingresar el correo"
                                    return@launch
                                }
                                if (contrasena.isBlank()) {
                                    mensaje = "Debe ingresar la contraseña"
                                    return@launch
                                }

                                cargando = true
                                try {
                                    SupabaseManager.client.auth.signInWith(Email) {
                                        email = correo.trim()
                                        password = contrasena
                                    }

                                    val userId = SupabaseManager.client.auth
                                        .currentUserOrNull()?.id
                                    if (userId == null) {
                                        mensaje = "No se pudo obtener el usuario"
                                        cargando = false
                                        return@launch
                                    }

                                    val usuario = SupabaseManager.client
                                        .from("usuarios")
                                        .select { filter { eq("id", userId) } }
                                        .decodeSingle<Usuario>()

                                    if (usuario.estado.trim().uppercase() != "ACTIVO") {
                                        mensaje = "Tu cuenta no está activa"
                                        cargando = false
                                        return@launch
                                    }

                                    irAHomePorRol(usuario)
                                } catch (e: Exception) {
                                    mensaje = "Correo o contraseña incorrectos"
                                } finally {
                                    cargando = false
                                }
                            }
                        },
                        habilitado = !cargando
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )
                Text(
                    text = "Regístrate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrimario,
                    modifier = Modifier.clickable { irARegistro() }
                )
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    modifier: Modifier = Modifier,
    registroExitoso: () -> Unit,
    volverALogin: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var nombreCompleto by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("CONDUCTOR") }
    var expanded by remember { mutableStateOf(false) }
    var licenciaNum by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val roles = listOf("CONDUCTOR", "MECANICO", "ENCARGADO")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registro",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Nombre completo")
        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nuevoTexto ->
                nombreCompleto = soloLetrasEspacios(nuevoTexto)
            },
            singleLine = true
        )
        Text("Cédula")
        OutlinedTextField(
            value = cedula,
            onValueChange = { nuevoTexto ->
                cedula = soloNumeros(nuevoTexto, LARGO_CEDULA_USUARIO)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text("Correo electrónico")
        OutlinedTextField(
            value = correo,
            onValueChange = { nuevoTexto -> correo = nuevoTexto },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Text("Teléfono")
        OutlinedTextField(
            value = telefono,
            onValueChange = { nuevoTexto ->
                telefono = soloNumeros(nuevoTexto, LARGO_TELEFONO_USUARIO)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text("Rol")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = rol,
                onValueChange = {},
                readOnly = true,
                label = { Text("") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roles.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            rol = opcion
                            expanded = false
                        }
                    )
                }
            }
        }
        if (rol == "CONDUCTOR") {
            Text("Número de licencia")
            OutlinedTextField(
                value = licenciaNum,
                onValueChange = { nuevoTexto -> licenciaNum = nuevoTexto },
                singleLine = true
            )
        }
        Text("Contraseña")
        OutlinedTextField(
            value = contrasena,
            onValueChange = { nuevoTexto -> contrasena = nuevoTexto },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val errorDatos = validarDatosUsuario(
                        nombreCompleto = nombreCompleto,
                        cedula = cedula,
                        correo = correo,
                        telefono = telefono,
                        rol = rol,
                        numeroLicencia = licenciaNum
                    )

                    if (errorDatos != null) {
                        mensaje = errorDatos
                        return@launch
                    }

                    val errorContrasena = validarContrasenaRegistro(contrasena)
                    if (errorContrasena != null) {
                        mensaje = errorContrasena
                        return@launch
                    }

                    try {
                        SupabaseManager.client.auth.signUpWith(Email) {
                            email = correo.trim()
                            password = contrasena
                        }

                        val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                        if (userId == null) {
                            mensaje = "No se pudo crear el usuario"
                            return@launch
                        }

                        val perfil = Usuario(
                            id = userId,
                            nombreCompleto = nombreCompleto.trim(),
                            cedula = cedula.trim(),
                            correo = correo.trim(),
                            telefono = telefono.trim(),
                            numeroLicencia = if (rol == "CONDUCTOR") licenciaNum.trim() else null,
                            rol = rol,
                            estado = "ACTIVO"
                        )

                        SupabaseManager.client.from("usuarios").insert(perfil)

                        mensaje = "Usuario registrado correctamente"
                        registroExitoso()
                    } catch (e: Exception) {
                        mensaje = "Error al registrar: ${e.message}"
                    }
                }
            }
        ) {
            Text("Registrar usuario")
        }
        Text(
            text = "¿Ya tienes una cuenta? Regresar a login",
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { volverALogin() }
        )
        Text(mensaje)
    }
}

@Composable
fun PantallaRecuperarCorreo(
    modifier: Modifier = Modifier,
    volverALogin: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Recuperar contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Correo electrónico")
        OutlinedTextField(
            value = correo,
            onValueChange = { nuevoTexto -> correo = nuevoTexto }
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    if (correo.isBlank()) {
                        mensaje = "Debe ingresar el correo"
                        return@launch
                    }
                    try {
                        SupabaseManager.client.auth.resetPasswordForEmail(
                            email = correo.trim(),
                            redirectUrl = "transandina://reset-password"
                        )
                        mensaje = "Se envió un enlace de recuperación a tu correo"
                    } catch (e: Exception) {
                        mensaje = "Error al enviar recuperación: ${e.message}"
                    }
                }
            }
        ) {
            Text("Enviar enlace de recuperación")
        }
        Text(
            text = "¿Recordaste tu contraseña? Regresar a login",
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { volverALogin() }
        )
        Text(mensaje)
    }
}

@Composable
fun PantallaCambiarContrasena(
    modifier: Modifier = Modifier,
    cambioExitoso: () -> Unit,
    volverALogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var contrasena by remember { mutableStateOf("") }
    var confirmacionContrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Cambiar contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Ingrese su nueva contraseña")
        OutlinedTextField(
            value = contrasena,
            onValueChange = { nuevoTexto -> contrasena = nuevoTexto },
            visualTransformation = PasswordVisualTransformation()
        )
        Text("Confirme su nueva contraseña")
        OutlinedTextField(
            value = confirmacionContrasena,
            onValueChange = { nuevoTexto -> confirmacionContrasena = nuevoTexto },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {
                scope.launch {
                    if (contrasena.isBlank() || confirmacionContrasena.isBlank()) {
                        mensaje = "Debe ingresar la nueva contraseña dos veces"
                        return@launch
                    }
                    if (contrasena != confirmacionContrasena) {
                        mensaje = "Las contraseñas no coinciden"
                        return@launch
                    }
                    if (contrasena.length < 6) {
                        mensaje = "La contraseña debe tener al menos 6 caracteres"
                        return@launch
                    }
                    try {
                        SupabaseManager.client.auth.updateUser {
                            password = contrasena
                        }
                        mensaje = "Se cambió exitosamente la contraseña"
                        cambioExitoso()
                    } catch (e: Exception) {
                        mensaje = "Error al cambiar contraseña: ${e.message}"
                    }
                }
            }
        ) {
            Text("Cambiar contraseña")
        }
        Text(
            text = "¿Recordaste tu contraseña? Regresar a login",
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { volverALogin() }
        )
        Text(mensaje)
    }
}
