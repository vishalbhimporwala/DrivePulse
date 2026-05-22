package com.drivepulse.gps.features.tools

import androidx.compose.runtime.Composable
import com.drivepulse.gps.design.Amber
import com.drivepulse.gps.design.Cyan
import com.drivepulse.gps.design.Mint
import com.drivepulse.gps.design.ScreenFrame
import com.drivepulse.gps.design.ToolCard

@Composable
fun ToolsScreen() {
    ScreenFrame(
        title = "Tools",
        subtitle = "Measurement utilities for map work.",
    ) {
        ToolCard("Distance", "Measure straight-line and route distance.", Mint)
        ToolCard("Area", "Draw land polygons with acre and hectare units.", Amber)
        ToolCard("Radius", "Visualize coverage for delivery, signal, or drone range.", Cyan)
    }
}
