package com.example.proyecto1ap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.graphics.Color
import com.example.proyecto1ap.ui.theme.Proyecto1APTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseManager.client.handleDeeplinks(intent)
        val abrirCambioContrasena=
            intent?.data?.scheme == "transandina" &&
                    intent?.data?.host == "reset-password"
        enableEdgeToEdge()
        setContent {
            Proyecto1APTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppPrincipal(modifier= Modifier.padding(innerPadding),
                        pantallaInicial= if (abrirCambioContrasena) "cambiarContrasena" else "login")
                }
            }
        }
    }
}

@Composable
fun AppPrincipal(
    modifier: Modifier,
    pantallaInicial: String = "login"
) {
    var pantallaActual by remember {mutableStateOf(pantallaInicial) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    when (pantallaActual) {
        "login" -> {
            PantallaInicial(
                irARegistro = {
                    pantallaActual = "registro"
                },
                irARecuperarCorreo = {
                    pantallaActual = "recuperarCorreo"
                },
                irAHomePorRol = { usuario ->
                    usuarioActual = usuario

                    pantallaActual = when (usuario.rol.trim().uppercase()) {
                        "CONDUCTOR" -> "homeConductor"
                        "MECANICO" -> "homeMecanico"
                        "ENCARGADO" -> "homeEncargado"
                        else -> "login"
                    }
                }
            )
        }

        "registro"->PantallaRegistro(
            volverALogin= {
                pantallaActual= "login"
            },
            registroExitoso= {
                pantallaActual= "login"
            }
        )

        "recuperarCorreo"->PantallaRecuperarCorreo(
            volverALogin= {
                pantallaActual= "login"
            }
        )

        "cambiarContrasena" -> PantallaCambiarContrasena(
            volverALogin = {
                pantallaActual = "login"
            },
            cambioExitoso = {
                pantallaActual = "login"
            }
        )

        "homeConductor"->PantallaHomeConductor(
            usuario = usuarioActual,
            cerrarSesion= {
                usuarioActual = null
                pantallaActual= "login"
            }
        )

        "homeMecanico"-> PantallaHomeMecanico(
            usuario = usuarioActual,
            cerrarSesion= {
                usuarioActual = null
                pantallaActual= "login"
            }
        )

        "homeEncargado"-> PantallaHomeEncargado(
            usuario = usuarioActual,
            cerrarSesion= {
                usuarioActual = null
                pantallaActual= "login"
            }
        )
    }
}

@Composable
fun PantallaInicial(
    modifier: Modifier = Modifier,
    irAHomePorRol:(Usuario)->Unit,
    irARecuperarCorreo: () -> Unit,
    irARegistro: () -> Unit,) {
    val scope= rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier= modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text= "TransAndina",
            fontSize= 28.sp,
            fontWeight= FontWeight.Bold
        )
        Text("Correo electrónico")
        OutlinedTextField(
            value= correo,
            onValueChange= { nuevoTexto ->
                correo= nuevoTexto
            }
        )
        Text("Contraseña")
        OutlinedTextField(
            value= contrasena,
            onValueChange= { nuevoTexto ->
                contrasena= nuevoTexto
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick= {
                scope.launch {
                    if (correo.isBlank()) {
                        mensaje = "Debe ingresar el correo"
                        return@launch
                    }

                    if (contrasena.isBlank()) {
                        mensaje = "Debe ingresar la contraseña"
                        return@launch
                    }

                    try {
                        SupabaseManager.client.auth.signInWith(Email) {
                            email= correo.trim()
                            password= contrasena
                        }

                        val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                        if (userId == null) {
                            mensaje = "No se pudo obtener el usuario"
                            return@launch
                        }
                        val usuario = SupabaseManager.client
                            .from("usuarios")
                            .select {
                                filter {
                                    eq("id", userId)
                                }
                            }
                            .decodeSingle<Usuario>()

                        if (usuario.estado.trim().uppercase() != "ACTIVO") {
                            mensaje = "Tu cuenta no está activa"
                            return@launch
                        }

                        mensaje= "Inicio de sesión exitoso"
                        irAHomePorRol(usuario)
                    } catch (e: Exception) {
                        mensaje= "Correo o contraseña incorrectos"
                    }
                }
            }
        ) {
            Text("Ingresar")
        }
        Text(
            text = "¿Olvidó su contraseña?",
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable {
                irARecuperarCorreo()
            }
        )
        Text(
            text = "¿No tienes una cuenta? Regístrate",
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable {
                irARegistro()
            }
        )
        Text(mensaje)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    modifier: Modifier = Modifier,
    registroExitoso: () -> Unit,
    volverALogin: () -> Unit
){
    val scope = rememberCoroutineScope()

    var nombre_completo by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("")}
    var rol by remember { mutableStateOf("CONDUCTOR")}
    var expanded by remember { mutableStateOf(false) }
    var LicenciaNum by remember { mutableStateOf("")}
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val roles = listOf("CONDUCTOR", "MECANICO", "ENCARGADO")
    Column(
        modifier= modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text= "Registro",
            fontSize= 28.sp,
            fontWeight= FontWeight.Bold
        )
        Text("Nombre completo")
        OutlinedTextField(
            value= nombre_completo,
            onValueChange= { nuevoTexto ->
                nombre_completo= nuevoTexto
            }
        )
        Text("Cédula")
        OutlinedTextField(
            value= cedula,
            onValueChange= { nuevoTexto ->
                cedula= nuevoTexto
            }
        )
        Text("Correo electrónico")
        OutlinedTextField(
            value= correo,
            onValueChange= { nuevoTexto ->
                correo= nuevoTexto
            }
        )
        Text("Teléfono")
        OutlinedTextField(
            value= telefono,
            onValueChange= { nuevoTexto ->
                telefono= nuevoTexto
            }
        )
        Text("Rol")
        ExposedDropdownMenuBox(
            expanded= expanded,
            onExpandedChange= {
                expanded= !expanded
            }
        ) {
            OutlinedTextField(
                value= rol,
                onValueChange= {},
                readOnly= true,
                label= { Text("") },
                trailingIcon= {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded= expanded)
                },
                modifier= Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded= expanded,
                onDismissRequest= {
                    expanded= false
                }
            ) {
                roles.forEach { opcion ->
                    DropdownMenuItem(
                        text= {
                            Text(opcion)
                        },
                        onClick = {
                            rol= opcion
                            expanded= false
                        }
                    )
                }
            }
        }
        if (rol=="CONDUCTOR"){
            Text("Número de licencia")
            OutlinedTextField(
                value= LicenciaNum,
                onValueChange= { nuevoTexto ->
                    LicenciaNum= nuevoTexto
                }
            )
        }
        Text("Contraseña")
        OutlinedTextField(
            value= contrasena,
            onValueChange= { nuevoTexto ->
                contrasena= nuevoTexto
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    if (nombre_completo.isBlank()) {
                        mensaje = "Debe ingresar el nombre completo"
                        return@launch
                    }

                    if (cedula.isBlank()) {
                        mensaje = "Debe ingresar la cédula"
                        return@launch
                    }

                    if (correo.isBlank()) {
                        mensaje = "Debe ingresar el correo"
                        return@launch
                    }

                    if (telefono.isBlank()) {
                        mensaje = "Debe ingresar el teléfono"
                        return@launch
                    }

                    if (contrasena.isBlank()) {
                        mensaje = "Debe ingresar la contraseña"
                        return@launch
                    }

                    if (rol == "CONDUCTOR" && LicenciaNum.isBlank()) {
                        mensaje = "Debe ingresar el número de licencia"
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
                            nombreCompleto = nombre_completo.trim(),
                            cedula = cedula.trim(),
                            correo = correo.trim(),
                            telefono = telefono.trim(),
                            numeroLicencia = if (rol == "CONDUCTOR") LicenciaNum.trim() else null,
                            rol = rol,
                            estado = "ACTIVO"
                        )

                        SupabaseManager.client
                            .from("usuarios")
                            .insert(perfil)

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
            modifier = Modifier.clickable {
                volverALogin()
            }
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
            onValueChange = { nuevoTexto ->
                correo = nuevoTexto
            }
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
            modifier = Modifier.clickable {
                volverALogin()
            }
        )
        Text(mensaje)
    }
}
@Composable
fun PantallaCambiarContrasena(
    modifier: Modifier = Modifier,
    cambioExitoso:()->Unit,
    volverALogin:()->Unit
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
            value= contrasena,
            onValueChange= { nuevoTexto ->
                contrasena= nuevoTexto
            },
            visualTransformation = PasswordVisualTransformation()
        )
        Text("Confirme su nueva contraseña")
        OutlinedTextField(
            value= confirmacionContrasena,
            onValueChange= { nuevoTexto ->
                confirmacionContrasena= nuevoTexto
            },
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
            modifier = Modifier.clickable {
                volverALogin()
            }
        )
        Text(mensaje)

    }

}
@Composable
fun PantallaHomeMecanico(
    usuario: Usuario?,
    cerrarSesion: () -> Unit
) {
    PantallaHomeRol(
        titulo = "Menú principal",
        usuario = usuario,
        opciones = listOf(
            "Registrar kilometraje",
            "Ver vehículo asignado",
            "Ver alertas",
            "Ver perfil"
        ),
        cerrarSesion = cerrarSesion
    )
}
@Composable
fun PantallaHomeConductor(
    usuario: Usuario?,
    cerrarSesion: () -> Unit
) {
    PantallaHomeRol(
        titulo = "Menú principal",
        usuario = usuario,
        opciones = listOf(
            "Registrar mantenimiento",
            "Historial de mantenimientos",
            "Ver alertas",
            "Ver perfil"
        ),
        cerrarSesion = cerrarSesion
    )
}
@Composable
fun PantallaHomeEncargado(
    usuario: Usuario?,
    cerrarSesion: () -> Unit
) {
    PantallaHomeRol(
        titulo = "Menú principal",
        usuario = usuario,
        opciones = listOf(
            "Gestión de flotilla",
            "Gestión de usuarios",
            "Centro de alertas",
            "Ver perfil"
        ),
        cerrarSesion = cerrarSesion
    )
}

@Composable
fun PantallaHomeRol(
    titulo: String,
    usuario: Usuario?,
    opciones: List<String>,
    cerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Hola, ${usuario?.nombreCompleto ?: "Usuario"}")
        Text("Rol: ${usuario?.rol ?: "Sin rol"}")
        Text("Correo: ${usuario?.correo ?: "No disponible"}")
        Text("Teléfono: ${usuario?.telefono ?: "No disponible"}")

        if (usuario?.numeroLicencia != null) {
            Text("Licencia: ${usuario.numeroLicencia}")
        }

        Text(
            text = "Opciones",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        opciones.forEach { opcion ->
            Button(
                onClick = {
                    // Tus compañeros conectan esta funcionalidad después
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(opcion)
            }
        }

        Button(
            onClick = cerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Proyecto1APTheme {
        PantallaRegistro(
            modifier = Modifier,
            registroExitoso = {},
            volverALogin = {}
        )
    }
}
