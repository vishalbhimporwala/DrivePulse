package com.drivepulse.gps.features.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.gps.design.Cyan
import com.drivepulse.gps.design.FeaturedCard
import com.drivepulse.gps.design.Line
import com.drivepulse.gps.design.MiniStat
import com.drivepulse.gps.design.Mint
import com.drivepulse.gps.design.Panel
import com.drivepulse.gps.design.PanelSoft
import com.drivepulse.gps.design.Red
import com.drivepulse.gps.design.ScreenFrame
import com.drivepulse.gps.design.TextMuted
import com.drivepulse.gps.design.TextPrimary

@Composable
fun TripsScreen() {
    ScreenFrame(
        title = "Trips",
        subtitle = "Routes, stats, and replay history.",
    ) {
        FeaturedCard("No trips yet", "Your first recorded drive will appear here with distance, duration, route path, and speed profile.")
        TimelinePreview()
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniStat("Week", "0", "trips", Mint, Modifier.weight(1f))
            MiniStat("Distance", "0.0", "km", Cyan, Modifier.weight(1f))
            MiniStat("Top", "0", "km/h", Red, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TimelinePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Panel)
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Trip timeline", fontWeight = FontWeight.Bold)
        listOf("Start", "Cruise", "Arrive").forEachIndexed { index, label ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (index == 1) Mint else PanelSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${index + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 1) Color(0xFF041109) else TextPrimary,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(label, modifier = Modifier.weight(1f))
                Text("--:--", color = TextMuted)
            }
        }
    }
}
