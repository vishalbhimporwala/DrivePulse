package com.drivepulse.gps

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(): platform.UIKit.UIViewController {
    val speedLocationProvider = IosSpeedLocationProvider()
    return ComposeUIViewController {
        App(speedLocationProvider = speedLocationProvider)
    }
}
