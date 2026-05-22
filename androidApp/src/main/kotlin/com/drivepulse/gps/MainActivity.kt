package com.drivepulse.gps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    private lateinit var speedLocationProvider: AndroidSpeedLocationProvider

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            speedLocationProvider.onPermissionResult()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        speedLocationProvider = AndroidSpeedLocationProvider(
            activity = this,
            permissionLauncher = locationPermissionLauncher,
        )

        setContent {
            App(speedLocationProvider = speedLocationProvider)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
