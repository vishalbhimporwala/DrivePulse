package com.drivepulse.gps.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.gps.design.Mint
import com.drivepulse.gps.design.TextMuted
import com.drivepulse.gps.design.TextPrimary

@Composable
fun DrivePulseBottomBar(
    selectedTab: DrivePulseTab,
    onTabSelected: (DrivePulseTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xF20B1016))
            .border(1.dp, Color(0xFF17212B))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DrivePulseTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Button(
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) Color(0xFF172330) else Color.Transparent,
                    contentColor = if (selected) TextPrimary else TextMuted,
                ),
                contentPadding = ButtonDefaults.TextButtonContentPadding,
                elevation = null,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    TabGlyph(tab = tab, selected = selected)
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun TabGlyph(tab: DrivePulseTab, selected: Boolean) {
    val color = if (selected) Mint else TextMuted
    Canvas(modifier = Modifier.size(20.dp)) {
        val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
        when (tab) {
            DrivePulseTab.Map -> {
                drawCircle(color, radius = size.minDimension * 0.36f, style = stroke)
                drawCircle(color, radius = 2.2.dp.toPx(), center = center)
            }
            DrivePulseTab.Speed -> {
                drawArc(color, 150f, 240f, false, style = stroke)
                drawLine(
                    color,
                    center,
                    Offset(size.width * 0.68f, size.height * 0.38f),
                    strokeWidth = 2.2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            DrivePulseTab.Trips -> {
                drawLine(color, Offset(size.width * 0.22f, size.height * 0.28f), Offset(size.width * 0.78f, size.height * 0.28f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.22f, size.height * 0.50f), Offset(size.width * 0.66f, size.height * 0.50f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * 0.22f, size.height * 0.72f), Offset(size.width * 0.52f, size.height * 0.72f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
            }
            DrivePulseTab.Tools -> {
                drawCircle(color, radius = size.minDimension * 0.28f, style = stroke)
                drawLine(color, Offset(size.width * 0.68f, size.height * 0.68f), Offset(size.width * 0.88f, size.height * 0.88f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
            }
            DrivePulseTab.Settings -> {
                drawCircle(color, radius = size.minDimension * 0.32f, style = stroke)
                drawCircle(color, radius = size.minDimension * 0.10f)
            }
        }
    }
}
