package com.drivepulse.gps.features.settings

import androidx.compose.runtime.Composable
import com.drivepulse.gps.design.Amber
import com.drivepulse.gps.design.Cyan
import com.drivepulse.gps.design.Mint
import com.drivepulse.gps.design.Red
import com.drivepulse.gps.design.ScreenFrame
import com.drivepulse.gps.design.SettingRow

@Composable
fun SettingsScreen() {
    ScreenFrame(
        title = "Settings",
        subtitle = "Personalize the driving cockpit.",
    ) {
        SettingRow("Units", "Kilometers", Cyan)
        SettingRow("Theme", "Dark", Mint)
        SettingRow("Trip storage", "On device", Amber)
        SettingRow("Premium", "Not active", Red)
    }
}
