package com.drivepulse.gps.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color

val AppBg = Color(0xFF070A0E)
val Panel = Color(0xFF101720)
val PanelSoft = Color(0xFF16212B)
val Line = Color(0xFF253241)
val TextPrimary = Color(0xFFF4F8FB)
val TextMuted = Color(0xFF95A4B3)
val Mint = Color(0xFF36D27D)
val Cyan = Color(0xFF4DB8FF)
val Amber = Color(0xFFFFC857)
val Red = Color(0xFFFF5A5F)

@Composable
fun DrivePulseTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppBg,
            contentColor = TextPrimary,
            content = content,
        )
    }
}
