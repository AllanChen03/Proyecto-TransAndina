package com.example.proyecto1ap.kilometraje

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.proyecto1ap.ui.theme.AzulPrimario
import com.example.proyecto1ap.ui.theme.Borde
import java.time.LocalDate

/**
 * Gráfica de línea. Los puntos deben venir ordenados de más antiguo a más reciente.
 */
@Composable
fun GraficaKilometraje(
    puntos: List<Pair<LocalDate, Int>>,
    modifier: Modifier = Modifier
) {
    if (puntos.size < 2) return

    val minKm = puntos.minOf { it.second }
    val maxKm = puntos.maxOf { it.second }
    val rangoKm = (maxKm - minKm).coerceAtLeast(1)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        val margen = 12.dp.toPx()
        val ancho = size.width - margen * 2
        val alto = size.height - margen * 2

        for (i in 0..2) {
            val y = margen + alto * i / 2f
            drawLine(
                color = Borde,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val offsets = puntos.mapIndexed { i, (_, km) ->
            val x = margen + i / (puntos.size - 1).toFloat() * ancho
            val y = margen + alto - (km - minKm) / rangoKm.toFloat() * alto
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(offsets.first().x, offsets.first().y)
            offsets.drop(1).forEach { lineTo(it.x, it.y) }
        }

        drawPath(
            path = path,
            color = AzulPrimario,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        offsets.forEach { drawCircle(AzulPrimario, radius = 4.dp.toPx(), center = it) }
    }
}