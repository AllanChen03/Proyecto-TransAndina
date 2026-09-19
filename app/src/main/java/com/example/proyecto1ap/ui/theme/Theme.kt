package com.example.proyecto1ap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    background = FondoApp,
    surface = Superficie,
    onSurface = TextoPrincipal,
    onBackground = TextoPrincipal,
    outline = Borde
)

@Composable
fun Proyecto1APTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}