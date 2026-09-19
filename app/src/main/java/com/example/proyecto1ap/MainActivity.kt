package com.example.proyecto1ap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.proyecto1ap.ui.theme.Proyecto1APTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseManager.client.handleDeeplinks(intent)

        val abrirCambioContrasena =
            intent?.data?.scheme == "transandina" &&
                    intent?.data?.host == "reset-password"

        enableEdgeToEdge()
        setContent {
            Proyecto1APTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppPrincipal(
                        modifier = Modifier.padding(innerPadding),
                        pantallaInicial = if (abrirCambioContrasena) "cambiarContrasena" else "login"
                    )
                }
            }
        }
    }
}