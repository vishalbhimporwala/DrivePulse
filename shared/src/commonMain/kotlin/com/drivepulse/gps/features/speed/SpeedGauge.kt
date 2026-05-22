package com.drivepulse.gps.features.speed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.drivepulse.gps.design.Mint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedGauge(
    speed: Int,
    maxSpeed: Int,
    modifier: Modifier = Modifier,
) {
    val sweep = (speed.toFloat() / maxSpeed.toFloat()).coerceIn(0f, 1f) * GaugeSweep

    Canvas(modifier = modifier.size(268.dp)) {
        val strokeWidth = 18.dp.toPx()
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

        drawArc(
            color = Color(0xFF233242),
            startAngle = GaugeStart,
            sweepAngle = GaugeSweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
        drawArc(
            color = Mint,
            startAngle = GaugeStart,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )

        for (index in 0..8) {
            val angle = (GaugeStart + index * 31f) * PI / 180.0
            val outer = size.minDimension * 0.45f
            val inner = size.minDimension * 0.40f
            val centerPoint = Offset(size.width / 2, size.height / 2)
            drawLine(
                color = Color(0xFF6B7C8D),
                start = Offset(
                    centerPoint.x + cos(angle).toFloat() * inner,
                    centerPoint.y + sin(angle).toFloat() * inner,
                ),
                end = Offset(
                    centerPoint.x + cos(angle).toFloat() * outer,
                    centerPoint.y + sin(angle).toFloat() * outer,
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

private const val GaugeStart = 146f
private const val GaugeSweep = 248f
