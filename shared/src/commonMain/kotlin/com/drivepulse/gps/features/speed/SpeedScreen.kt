package com.drivepulse.gps.features.speed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.gps.design.AppBg
import com.drivepulse.gps.design.Amber
import com.drivepulse.gps.design.Cyan
import com.drivepulse.gps.design.Line
import com.drivepulse.gps.design.MiniStat
import com.drivepulse.gps.design.Mint
import com.drivepulse.gps.design.Panel
import com.drivepulse.gps.design.PanelSoft
import com.drivepulse.gps.design.Red
import com.drivepulse.gps.design.ScreenFrame
import com.drivepulse.gps.design.TextMuted

@Composable
fun SpeedScreen(
    speedLocationProvider: SpeedLocationProvider = PreviewSpeedLocationProvider,
    onFullScreenChanged: (Boolean) -> Unit = {},
) {
    var sessionState by remember { mutableStateOf(SpeedSessionState()) }
    var isFullScreen by remember { mutableStateOf(false) }
    var isHudMode by remember { mutableStateOf(false) }
    val reading = sessionState.reading

    DisposableEffect(speedLocationProvider) {
        speedLocationProvider.start(
            onSample = { sample ->
                sessionState = sessionState.withSample(sample)
            },
            onStatusChanged = { status ->
                sessionState = sessionState.copy(status = status)
            },
        )

        onDispose {
            speedLocationProvider.stop()
        }
    }

    if (isFullScreen || isHudMode) {
        FullScreenSpeedMode(
            reading = reading,
            status = sessionState.status,
            smoothingLabel = sessionState.smoothingLabel,
            isHudMode = isHudMode,
            onExit = {
                isFullScreen = false
                isHudMode = false
                onFullScreenChanged(false)
            },
        )
        return
    }

    ScreenFrame(
        title = "Speed",
        subtitle = "Live speed, accuracy, and driving stats.",
    ) {
        if (sessionState.status == SpeedLocationStatus.PermissionMissing) {
            SpeedPermissionCard(onRequestPermission = speedLocationProvider::requestLocationPermission)
        } else if (sessionState.status == SpeedLocationStatus.ProviderDisabled) {
            SpeedStatusCard(
                title = "Location is turned off",
                body = "Turn on device location services to start live speed tracking.",
                accent = Amber,
            )
        } else if (sessionState.status == SpeedLocationStatus.Unavailable) {
            SpeedStatusCard(
                title = "GPS is unavailable",
                body = "DrivePulse could not start location updates. Try again after checking device location settings.",
                accent = Red,
            )
        }
        SpeedHero(
            reading = reading,
            status = sessionState.status,
            smoothingLabel = sessionState.smoothingLabel,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniStat("Max", reading.maxSpeed.toString(), reading.unit.shortLabel.lowercase(), Red, Modifier.weight(1f))
            MiniStat("Average", reading.averageSpeed.toString(), reading.unit.shortLabel.lowercase(), Cyan, Modifier.weight(1f))
            MiniStat("Accuracy", reading.accuracyLabel, "gps", Amber, Modifier.weight(1f))
        }
        UnitSelector(
            selectedUnit = reading.unit,
            onUnitSelected = { unit ->
                sessionState = sessionState.withUnit(unit)
            },
        )
        SpeedActionGrid(
            onFullScreen = {
                isFullScreen = true
                onFullScreenChanged(true)
            },
            onHudMode = {
                isHudMode = true
                onFullScreenChanged(true)
            },
        )
    }
}

@Composable
private fun FullScreenSpeedMode(
    reading: SpeedReading,
    status: SpeedLocationStatus,
    smoothingLabel: String,
    isHudMode: Boolean,
    onExit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF111C25), AppBg, Color(0xFF020304)),
                ),
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusDotLabel(
                label = if (isHudMode) "HUD mode" else status.displayLabel,
                color = if (reading.isGpsLocked) Mint else Amber,
            )
            Button(
                onClick = onExit,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PanelSoft,
                    contentColor = Color.White,
                ),
                elevation = null,
            ) {
                Text("Exit", fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = if (isHudMode) Modifier.graphicsLayer(scaleX = -1f) else Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = reading.currentSpeed.toString(),
                fontSize = 132.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 132.sp,
                letterSpacing = 0.sp,
            )
            Text(
                text = reading.unit.shortLabel,
                color = TextMuted,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FullScreenMetric("Max", reading.maxSpeed.toString(), reading.unit.shortLabel.lowercase(), Red, Modifier.weight(1f))
                FullScreenMetric("Accuracy", reading.accuracyLabel, "gps", Amber, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FullScreenMetric("Average", reading.averageSpeed.toString(), reading.unit.shortLabel.lowercase(), Cyan, Modifier.weight(1f))
                FullScreenMetric("Smooth", smoothingLabel, "filter", Mint, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FullScreenMetric(
    label: String,
    value: String,
    unit: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x99101720))
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(accent, CircleShape),
        )
        Text(label, color = TextMuted, fontSize = 12.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, maxLines = 1)
        Text(unit, color = TextMuted, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun SpeedStatusCard(
    title: String,
    body: String,
    accent: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PanelSoft)
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(accent, CircleShape),
        )
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = body,
            color = TextMuted,
            lineHeight = 19.sp,
        )
    }
}

@Composable
private fun SpeedPermissionCard(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PanelSoft)
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Enable precise location", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "DrivePulse needs location access to calculate live GPS speed, accuracy, max speed, and average speed.",
            color = TextMuted,
            lineHeight = 19.sp,
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Color(0xFF041109)),
        ) {
            Text("Allow Location", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SpeedHero(
    reading: SpeedReading,
    status: SpeedLocationStatus,
    smoothingLabel: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF162533), Panel, Color(0xFF0C1117)),
                ),
            )
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SpeedStatusBar(
            reading = reading,
            status = status,
        )
        Box(contentAlignment = Alignment.Center) {
            SpeedGauge(speed = reading.currentSpeed, maxSpeed = 180)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = reading.currentSpeed.toString(),
                    fontSize = 76.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                )
                Text(
                    text = reading.unit.shortLabel,
                    color = TextMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SpeedMicroPanel("Mode", "Digital", Mint, Modifier.weight(1f))
            SpeedMicroPanel("GPS", status.signalLabel, Amber, Modifier.weight(1f))
            SpeedMicroPanel("Smooth", smoothingLabel, Cyan, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SpeedStatusBar(
    reading: SpeedReading,
    status: SpeedLocationStatus,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x99070A0E))
            .border(1.dp, Color(0x663A4A5B), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDotLabel(
            label = status.displayLabel,
            color = if (reading.isGpsLocked) Mint else Amber,
        )
        Text(
            text = "Precision ${reading.accuracyLabel}",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private val SpeedLocationStatus.signalLabel: String
    get() = when (this) {
        SpeedLocationStatus.PermissionMissing -> "Permission"
        SpeedLocationStatus.Searching -> "Search"
        SpeedLocationStatus.GpsLocked -> "Strong"
        SpeedLocationStatus.ProviderDisabled -> "Disabled"
        SpeedLocationStatus.Unavailable -> "Offline"
    }

private val SpeedLocationStatus.displayLabel: String
    get() = when (this) {
        SpeedLocationStatus.PermissionMissing -> "Location needed"
        SpeedLocationStatus.Searching -> "Searching GPS"
        SpeedLocationStatus.GpsLocked -> "GPS locked"
        SpeedLocationStatus.ProviderDisabled -> "Location disabled"
        SpeedLocationStatus.Unavailable -> "GPS unavailable"
    }

@Composable
private fun StatusDotLabel(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SpeedMicroPanel(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x99101720))
            .border(1.dp, Color(0x663A4A5B), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(accent, CircleShape),
        )
        Spacer(Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, color = TextMuted, fontSize = 11.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
        }
    }
}

@Composable
private fun UnitSelector(
    selectedUnit: SpeedUnit,
    onUnitSelected: (SpeedUnit) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Panel)
            .border(1.dp, Line, RoundedCornerShape(8.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SpeedUnit.entries.forEach { unit ->
            val selected = unit == selectedUnit
            Button(
                onClick = { onUnitSelected(unit) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) Mint else Color.Transparent,
                    contentColor = if (selected) Color(0xFF041109) else TextMuted,
                ),
                elevation = null,
            ) {
                Text(unit.shortLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SpeedActionGrid(
    onFullScreen: () -> Unit,
    onHudMode: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SpeedActionCard("Full screen", "Large display for dashboard mounting.", Mint, Modifier.weight(1f), onClick = onFullScreen)
            SpeedActionCard("HUD mode", "Mirror layout for windshield view.", Cyan, Modifier.weight(1f), onClick = onHudMode)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SpeedActionCard("Speed alert", "Custom limit warning and voice alert.", Amber, Modifier.weight(1f))
            SpeedActionCard("Smooth GPS", "Filtered speed for stable readings.", Red, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SpeedActionCard(
    title: String,
    body: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Line, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PanelSoft, contentColor = Color.White),
        contentPadding = PaddingValues(14.dp),
        elevation = null,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accent, CircleShape),
            )
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Text(
                text = body,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
