package com.drivepulse.gps.features.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.gps.design.Amber
import com.drivepulse.gps.design.Cyan
import com.drivepulse.gps.design.GlassLabel
import com.drivepulse.gps.design.MiniStat
import com.drivepulse.gps.design.Mint
import com.drivepulse.gps.design.PrimaryActionRow
import com.drivepulse.gps.design.Red
import com.drivepulse.gps.design.ScreenFrame

@Composable
fun MapScreen() {
    ScreenFrame(
        title = "DrivePulse",
        subtitle = "Traffic map and driving cockpit.",
    ) {
        PremiumMapPanel()
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniStat("Speed", "0", "km/h", Mint, Modifier.weight(1f))
            MiniStat("Today", "0.0", "km", Cyan, Modifier.weight(1f))
            MiniStat("Traffic", "Clear", "nearby", Amber, Modifier.weight(1f))
        }
        PrimaryActionRow(primary = "Start Drive", secondary = "Street View")
    }
}

@Composable
private fun PremiumMapPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF111922))
            .border(1.dp, Color(0xFF233344), RoundedCornerShape(8.dp)),
    ) {
        StylizedMap()
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GlassLabel("Traffic layer", "Ready")
            GlassLabel("Map style", "Night")
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RoundMapControl("+")
            RoundMapControl("-")
            RoundMapControl("N")
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TrafficChip("Fast", Mint)
            TrafficChip("Slow", Amber)
            TrafficChip("Heavy", Red)
        }
    }
}

@Composable
private fun StylizedMap() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            Brush.linearGradient(
                colors = listOf(Color(0xFF182532), Color(0xFF0C1218), Color(0xFF1A2028)),
                start = Offset.Zero,
                end = Offset(size.width, size.height),
            ),
        )

        val minorRoad = Color(0xFF263746)
        val mainRoad = Color(0xFF445B70)

        for (i in -2..8) {
            val x = size.width * (i / 6f)
            drawLine(minorRoad, Offset(x, 0f), Offset(x + size.width * 0.34f, size.height), strokeWidth = 2.dp.toPx())
        }
        for (i in 0..7) {
            val y = size.height * (i / 6f)
            drawLine(minorRoad, Offset(0f, y), Offset(size.width, y - size.height * 0.22f), strokeWidth = 2.dp.toPx())
        }

        val arterial = Path().apply {
            moveTo(size.width * 0.04f, size.height * 0.72f)
            cubicTo(size.width * 0.28f, size.height * 0.60f, size.width * 0.40f, size.height * 0.30f, size.width * 0.72f, size.height * 0.34f)
            cubicTo(size.width * 0.86f, size.height * 0.36f, size.width * 0.94f, size.height * 0.20f, size.width, size.height * 0.14f)
        }
        drawPath(arterial, mainRoad, style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round))
        drawPath(arterial, Mint, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))

        val slowRoad = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.18f)
            cubicTo(size.width * 0.34f, size.height * 0.30f, size.width * 0.46f, size.height * 0.48f, size.width * 0.58f, size.height * 0.84f)
        }
        drawPath(slowRoad, Amber, style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round))

        drawCircle(Cyan, radius = 8.dp.toPx(), center = Offset(size.width * 0.44f, size.height * 0.52f))
        drawCircle(Color.White.copy(alpha = 0.28f), radius = 22.dp.toPx(), center = Offset(size.width * 0.44f, size.height * 0.52f), style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
private fun RoundMapControl(label: String) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xE6101720))
            .border(1.dp, Color(0x663A4A5B), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TrafficChip(label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xD90B1118))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape),
        )
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
