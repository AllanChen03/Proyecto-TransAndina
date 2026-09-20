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

private const val MIN_CONTRASENA = 8

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
                                        SupabaseManager.client.auth.signOut()
                                        mensaje = if (usuario.estado.trim().uppercase() == "SUSPENDIDO")
                                            "Tu cuenta está suspendida. Contactá al encargado de flota."
                                        else
                                            "Tu cuenta está desactivada."
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
    var verContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    val roles = listOf("CONDUCTOR", "MECANICO", "ENCARGADO")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoApp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registro",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        CampoTexto("Nombre completo", nombreCompleto, { nombreCompleto = it })
        CampoTexto("Cédula", cedula, { cedula = it })
        CampoTexto(
            "Correo electrónico", correo, { correo = it },
            tipoTeclado = KeyboardType.Email
        )
        CampoTexto(
            "Teléfono", telefono, { telefono = it },
            tipoTeclado = KeyboardType.Phone
        )

        Column {
            Text(
                text = "Rol",
                fontSize = 13.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = rol,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrimario,
                        unfocusedBorderColor = Borde,
                        focusedContainerColor = Superficie,
                        unfocusedContainerColor = Superficie
                    )
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
        }

        if (rol == "CONDUCTOR") {
            CampoTexto("Número de licencia", licenciaNum, { licenciaNum = it })
        }

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
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (contrasena.isBlank() || contrasena.length >= MIN_CONTRASENA)
                    "Mínimo $MIN_CONTRASENA caracteres"
                else
                    "Faltan ${MIN_CONTRASENA - contrasena.length} caracteres",
                fontSize = 12.sp,
                color = if (contrasena.isNotBlank() && contrasena.length < MIN_CONTRASENA)
                    RojoTexto
                else
                    TextoSecundario
            )
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                fontSize = 13.sp,
                color = RojoTexto
            )
        }

        Spacer(Modifier.height(4.dp))

        BotonPrimario(
            texto = "Registrar usuario",
            onClick = {
                scope.launch {
                    mensaje = ""

                    if (nombreCompleto.isBlank()) {
                        mensaje = "Debe ingresar el nombre completo"
                        return@launch
                    }
                    if (cedula.isBlank()) {
                        mensaje = "Debe ingresar la cédula"
                        return@launch
                    }

                    // ← Acá se declara, después de verificar que no esté vacía
                    val cedulaLimpia = cedula.filter { it.isDigit() }
                    if (cedulaLimpia.length < 9) {
                        mensaje = "La cédula debe tener al menos 9 dígitos"
                        return@launch
                    }

                    if (correo.isBlank()) {
                        mensaje = "Debe ingresar el correo"
                        return@launch
                    }
                    if (!correo.contains("@") || !correo.contains(".")) {
                        mensaje = "Ingrese un correo válido"
                        return@launch
                    }
                    if (telefono.isBlank()) {
                        mensaje = "Debe ingresar el teléfono"
                        return@launch
                    }
                    if (rol == "CONDUCTOR" && licenciaNum.isBlank()) {
                        mensaje = "Debe ingresar el número de licencia"
                        return@launch
                    }
                    if (contrasena.isBlank()) {
                        mensaje = "Debe ingresar la contraseña"
                        return@launch
                    }
                    if (contrasena.length < MIN_CONTRASENA) {
                        mensaje = "La contraseña debe tener al menos $MIN_CONTRASENA caracteres"
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
                            cedula = cedulaLimpia,        // ← Acá se usa
                            correo = correo.trim(),
                            telefono = telefono.trim(),
                            numeroLicencia = if (rol == "CONDUCTOR") licenciaNum.trim() else null,
                            rol = rol,
                            estado = "ACTIVO"
                        )

                        SupabaseManager.client.from("usuarios").insert(perfil)

                        registroExitoso()
                    } catch (e: Exception) {
                        val texto = e.message ?: ""
                        mensaje = when {
                            texto.contains("usuarios_cedula_key") ->
                                "Ya existe un usuario con esa cédula"
                            texto.contains("usuarios_correo_key") ->
                                "Ya existe un usuario con ese correo"
                            texto.contains("idx_licencia_unica") ->
                                "Ya existe un usuario con esa licencia"
                            texto.contains("already registered") ->
                                "Ese correo ya está registrado"
                            else -> "Error al registrar: ${e.message}"
                        }
                    }
                }
            }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "¿Ya tienes una cuenta? Iniciar sesión",
                fontSize = 14.sp,
                color = AzulPrimario,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { volverALogin() }
            )
        }

        Spacer(Modifier.height(24.dp))
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
    var enviado by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoApp)
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Recuperar contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Te enviaremos un enlace para restablecer tu contraseña",
            fontSize = 14.sp,
            color = TextoSecundario
        )

        Spacer(Modifier.height(8.dp))

        CampoTexto(
            etiqueta = "Correo electrónico",
            valor = correo,
            onValorChange = { correo = it },
            placeholder = "nombre@correo.com",
            tipoTeclado = KeyboardType.Email
        )

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                fontSize = 13.sp,
                color = if (enviado) TextoSecundario else RojoTexto
            )
        }

        Spacer(Modifier.height(4.dp))

        BotonPrimario(
            texto = "Enviar enlace de recuperación",
            onClick = {
                scope.launch {
                    mensaje = ""
                    if (correo.isBlank()) {
                        mensaje = "Debe ingresar el correo"
                        return@launch
                    }

                    try {
                        SupabaseManager.client.auth.resetPasswordForEmail(
                            email = correo.trim(),
                            redirectUrl = "transandina://reset-password"
                        )
                        enviado = true
                        mensaje = "Se envió un enlace de recuperación a tu correo"
                    } catch (e: Exception) {
                        enviado = false
                        mensaje = "Error al enviar recuperación: ${e.message}"
                    }
                }
            }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Regresar a inicio de sesión",
                fontSize = 14.sp,
                color = AzulPrimario,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { volverALogin() }
            )
        }
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
    var confirmacion by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    val coinciden = confirmacion.isBlank() || contrasena == confirmacion

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoApp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Cambiar contraseña",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Column {
            Text(
                text = "Nueva contraseña",
                fontSize = 13.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                modifier = Modifier.fillMaxWidth(),
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
                            contentDescription = null,
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
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (contrasena.isBlank() || contrasena.length >= MIN_CONTRASENA)
                    "Mínimo $MIN_CONTRASENA caracteres"
                else
                    "Faltan ${MIN_CONTRASENA - contrasena.length} caracteres",
                fontSize = 12.sp,
                color = if (contrasena.isNotBlank() && contrasena.length < MIN_CONTRASENA)
                    RojoTexto
                else
                    TextoSecundario
            )
        }

        Column {
            Text(
                text = "Confirmar contraseña",
                fontSize = 13.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = confirmacion,
                onValueChange = { confirmacion = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                isError = !coinciden,
                visualTransformation = if (verContrasena)
                    VisualTransformation.None
                else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulPrimario,
                    unfocusedBorderColor = Borde,
                    errorBorderColor = RojoTexto,
                    focusedContainerColor = Superficie,
                    unfocusedContainerColor = Superficie
                )
            )
            if (!coinciden) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Las contraseñas no coinciden",
                    fontSize = 12.sp,
                    color = RojoTexto
                )
            }
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                fontSize = 13.sp,
                color = RojoTexto
            )
        }

        Spacer(Modifier.height(4.dp))

        BotonPrimario(
            texto = "Cambiar contraseña",
            onClick = {
                scope.launch {
                    mensaje = ""

                    if (contrasena.isBlank() || confirmacion.isBlank()) {
                        mensaje = "Debe ingresar la nueva contraseña dos veces"
                        return@launch
                    }
                    if (contrasena != confirmacion) {
                        mensaje = "Las contraseñas no coinciden"
                        return@launch
                    }
                    if (contrasena.length < MIN_CONTRASENA) {
                        mensaje = "La contraseña debe tener al menos $MIN_CONTRASENA caracteres"
                        return@launch
                    }

                    try {
                        SupabaseManager.client.auth.updateUser {
                            password = contrasena
                        }
                        cambioExitoso()
                    } catch (e: Exception) {
                        mensaje = "Error al cambiar contraseña: ${e.message}"
                    }
                }
            },
            habilitado = coinciden
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Regresar a inicio de sesión",
                fontSize = 14.sp,
                color = AzulPrimario,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { volverALogin() }
            )
        }
    }
}