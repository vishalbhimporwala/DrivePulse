package com.drivepulse.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.drivepulse.gps.features.speed.SpeedLocationProvider
import com.drivepulse.gps.features.speed.SpeedLocationSample
import com.drivepulse.gps.features.speed.SpeedLocationStatus

class AndroidSpeedLocationProvider(
    private val activity: ComponentActivity,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
) : SpeedLocationProvider {
    private val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var listener: LocationListener? = null
    private var onSample: ((SpeedLocationSample) -> Unit)? = null
    private var onStatusChanged: ((SpeedLocationStatus) -> Unit)? = null

    override val hasLocationPermission: Boolean
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun requestLocationPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    @SuppressLint("MissingPermission")
    override fun start(
        onSample: (SpeedLocationSample) -> Unit,
        onStatusChanged: (SpeedLocationStatus) -> Unit,
    ) {
        this.onSample = onSample
        this.onStatusChanged = onStatusChanged
        listener?.let(locationManager::removeUpdates)
        listener = null

        if (!hasLocationPermission) {
            onStatusChanged(SpeedLocationStatus.PermissionMissing)
            return
        }

        val provider = bestProvider()
        if (provider == null) {
            onStatusChanged(SpeedLocationStatus.ProviderDisabled)
            return
        }

        onStatusChanged(SpeedLocationStatus.Searching)

        val nextListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onStatusChanged(SpeedLocationStatus.GpsLocked)
                onSample(
                    SpeedLocationSample(
                        speedMetersPerSecond = location.speed.takeIf { location.hasSpeed() }?.toDouble() ?: 0.0,
                        accuracyMeters = location.accuracy.takeIf { location.hasAccuracy() }?.toDouble(),
                        timestampMillis = location.time,
                    ),
                )
            }

            override fun onProviderEnabled(provider: String) {
                onStatusChanged(SpeedLocationStatus.Searching)
            }

            override fun onProviderDisabled(provider: String) {
                onStatusChanged(SpeedLocationStatus.ProviderDisabled)
            }

            @Deprecated("Required for API compatibility below Android R.")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }

        listener = nextListener
        try {
            locationManager.requestLocationUpdates(
                provider,
                UpdateIntervalMillis,
                MinDistanceMeters,
                nextListener,
                Looper.getMainLooper(),
            )

            locationManager.getLastKnownLocation(provider)
                ?.takeIf { System.currentTimeMillis() - it.time <= LastKnownLocationMaxAgeMillis }
                ?.let(nextListener::onLocationChanged)
        } catch (_: SecurityException) {
            onStatusChanged(SpeedLocationStatus.PermissionMissing)
        } catch (_: IllegalArgumentException) {
            onStatusChanged(SpeedLocationStatus.Unavailable)
        }
    }

    override fun stop() {
        listener?.let(locationManager::removeUpdates)
        listener = null
        onSample = null
        onStatusChanged = null
    }

    fun onPermissionResult() {
        val sampleCallback = onSample
        val statusCallback = onStatusChanged
        if (sampleCallback != null && statusCallback != null) {
            start(sampleCallback, statusCallback)
        }
    }

    private fun bestProvider(): String? =
        when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val UpdateIntervalMillis = 750L
        const val MinDistanceMeters = 0f
        const val LastKnownLocationMaxAgeMillis = 15_000L
    }
}
