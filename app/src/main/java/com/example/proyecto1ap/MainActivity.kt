package com.example.proyecto1ap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.proyecto1ap.ui.theme.Proyecto1APTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto1APTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicial(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaInicial(modifier: Modifier= Modifier) {
    val scope= rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier= modifier
            .fillMaxSize()
            .padding(24.dp)
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
            value= password,
            onValueChange= { nuevoTexto ->
                password= nuevoTexto
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick= {
                scope.launch {
                    try {
                        SupabaseManager.client.auth.signInWith(Email) {
                            email= correo
                            password= password
                        }
                        mensaje= "Inicio de sesión exitoso"
                    } catch (e: Exception) {
                        mensaje= "Correo o contraseña incorrectos"
                    }
                }
            }
        ) {
            Text("Ingresar")
        }

        Text(mensaje)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(modifier: Modifier = Modifier){
    val scope = rememberCoroutineScope()

    var nombre_completo by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("")}
    var rol by remember { mutableStateOf("CONDUCTOR")}
    var expanded by remember { mutableStateOf(false) }
    var LicenciaNum by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val roles = listOf("CONDUCTOR", "MECANICO", "ENCARGADO")
    Column(
        modifier= modifier
            .fillMaxSize()
            .padding(24.dp)
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
                },
                visualTransformation = PasswordVisualTransformation()
            )
        }

        Text("Contraseña")
        OutlinedTextField(
            value= password,
            onValueChange= { nuevoTexto ->
                password= nuevoTexto
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick= {
                scope.launch {
                    try {
                        SupabaseManager.client.auth.signUpWith(Email) {
                            email = correo
                            password = password
                        }

                        val userId = SupabaseManager.client.auth.currentUserOrNull()?.id

                        if (userId == null) {
                            Log.e("PRUEBA", "No hay sesión activa")
                            return@launch
                        }

                        val perfil= Usuario(
                            id= userId,
                            nombreCompleto= nombre_completo,
                            cedula= cedula,
                            correo= correo,
                            telefono= telefono,
                            numeroLicencia= if (rol == "CONDUCTOR") LicenciaNum else null,
                            rol= rol,
                            estado= "ACTIVO"
                        )
                        SupabaseManager.client.from("usuarios").insert(perfil)
                        mensaje= "Usuario registrado correctamente"
                    } catch (e: Exception) {
                        mensaje= "Error al registrar: ${e.message}"
                    }
                }
            }
        ) {
            Text("Ingresar")
        }

        Text(mensaje)
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Proyecto1APTheme {
        PantallaRegistro()
    }
}