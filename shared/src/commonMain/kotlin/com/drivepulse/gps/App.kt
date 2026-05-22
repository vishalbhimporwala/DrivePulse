package com.drivepulse.gps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.drivepulse.gps.design.AppBg
import com.drivepulse.gps.design.DrivePulseTheme
import com.drivepulse.gps.features.map.MapScreen
import com.drivepulse.gps.features.settings.SettingsScreen
import com.drivepulse.gps.features.speed.PreviewSpeedLocationProvider
import com.drivepulse.gps.features.speed.SpeedLocationProvider
import com.drivepulse.gps.features.speed.SpeedScreen
import com.drivepulse.gps.features.tools.ToolsScreen
import com.drivepulse.gps.features.trips.TripsScreen
import com.drivepulse.gps.navigation.DrivePulseBottomBar
import com.drivepulse.gps.navigation.DrivePulseTab

@Composable
@Preview
fun App(
    speedLocationProvider: SpeedLocationProvider = PreviewSpeedLocationProvider,
) {
    DrivePulseTheme {
        DrivePulseApp(speedLocationProvider = speedLocationProvider)
    }
}

@Composable
private fun DrivePulseApp(speedLocationProvider: SpeedLocationProvider) {
    var selectedTab by remember { mutableStateOf(DrivePulseTab.Map) }
    var isSpeedFullScreen by remember { mutableStateOf(false) }
    val showSpeedFullScreen = selectedTab == DrivePulseTab.Speed && isSpeedFullScreen

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            if (!showSpeedFullScreen) {
                DrivePulseBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        isSpeedFullScreen = false
                    },
                )
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showSpeedFullScreen) PaddingValues() else contentPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0D141B), AppBg, Color(0xFF05070A)),
                    ),
                ),
        ) {
            when (selectedTab) {
                DrivePulseTab.Map -> MapScreen()
                DrivePulseTab.Speed -> SpeedScreen(
                    speedLocationProvider = speedLocationProvider,
                    onFullScreenChanged = { isSpeedFullScreen = it },
                )
                DrivePulseTab.Trips -> TripsScreen()
                DrivePulseTab.Tools -> ToolsScreen()
                DrivePulseTab.Settings -> SettingsScreen()
            }
        }
    }
}
