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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    PantallaPrueba(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaPrueba(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Flotilla TransAndina",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                try {
                    val correo = "allan.conductor1@gmail.com"

                    SupabaseManager.client.auth.signUpWith(Email) {
                        email = correo
                        password = "test123456"
                    }

                    val userId = SupabaseManager.client.auth.currentUserOrNull()?.id
                    Log.d("PRUEBA", "Auth creado, id: $userId")

                    if (userId == null) {
                        Log.e("PRUEBA", "No hay sesión activa")
                        return@launch
                    }

                    val perfil = Usuario(
                        id = userId,
                        nombreCompleto = "Allan Conductor",
                        cedula = "112340567",
                        correo = correo,
                        telefono = "88887777",
                        numeroLicencia = "B1-12345",
                        rol = "CONDUCTOR"
                    )

                    SupabaseManager.client.from("usuarios").insert(perfil)
                    Log.d("PRUEBA", "Perfil insertado")

                } catch (e: Exception) {
                    Log.e("PRUEBA", "Error: ${e.message}", e)
                }
            }
        }) {
            Text("Crear usuario de prueba")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Proyecto1APTheme {
        PantallaPrueba()
    }
}