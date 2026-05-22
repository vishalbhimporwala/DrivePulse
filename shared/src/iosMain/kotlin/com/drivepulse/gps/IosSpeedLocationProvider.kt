package com.drivepulse.gps

import com.drivepulse.gps.features.speed.SpeedLocationProvider
import com.drivepulse.gps.features.speed.SpeedLocationSample
import com.drivepulse.gps.features.speed.SpeedLocationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLDistanceFilterNone
import platform.CoreLocation.kCLLocationAccuracyBestForNavigation
import platform.Foundation.NSError
import platform.darwin.NSObject

class IosSpeedLocationProvider : SpeedLocationProvider {
    private val locationManager = CLLocationManager()
    private val locationDelegate = IosLocationDelegate(
        onLocationUpdated = { location ->
            onStatusChanged?.invoke(SpeedLocationStatus.GpsLocked)
            onSample?.invoke(
                SpeedLocationSample(
                    speedMetersPerSecond = location.speed.takeIf { it >= 0.0 } ?: 0.0,
                    accuracyMeters = location.horizontalAccuracy.takeIf { it >= 0.0 },
                    timestampMillis = 0L,
                ),
            )
        },
        onLocationFailed = {
            onStatusChanged?.invoke(SpeedLocationStatus.Unavailable)
        },
        onAuthorizationChanged = {
            handleAuthorizationChanged()
        },
    )

    private var onSample: ((SpeedLocationSample) -> Unit)? = null
    private var onStatusChanged: ((SpeedLocationStatus) -> Unit)? = null

    init {
        locationManager.delegate = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        locationManager.distanceFilter = kCLDistanceFilterNone
    }

    override val hasLocationPermission: Boolean
        get() = CLLocationManager.authorizationStatus() == kCLAuthorizationStatusAuthorizedWhenInUse ||
            CLLocationManager.authorizationStatus() == kCLAuthorizationStatusAuthorizedAlways

    override fun requestLocationPermission() {
        locationManager.requestWhenInUseAuthorization()
    }

    override fun start(
        onSample: (SpeedLocationSample) -> Unit,
        onStatusChanged: (SpeedLocationStatus) -> Unit,
    ) {
        this.onSample = onSample
        this.onStatusChanged = onStatusChanged

        if (!CLLocationManager.locationServicesEnabled()) {
            onStatusChanged(SpeedLocationStatus.ProviderDisabled)
            return
        }

        if (!hasLocationPermission) {
            val authorizationStatus = CLLocationManager.authorizationStatus()
            if (authorizationStatus == kCLAuthorizationStatusDenied || authorizationStatus == kCLAuthorizationStatusRestricted) {
                onStatusChanged(SpeedLocationStatus.PermissionMissing)
            } else {
                onStatusChanged(SpeedLocationStatus.PermissionMissing)
            }
            return
        }

        onStatusChanged(SpeedLocationStatus.Searching)
        locationManager.startUpdatingLocation()
    }

    override fun stop() {
        locationManager.stopUpdatingLocation()
        onSample = null
        onStatusChanged = null
    }

    private fun handleAuthorizationChanged() {
        when {
            hasLocationPermission -> {
                val sampleCallback = onSample
                val statusCallback = onStatusChanged
                if (sampleCallback != null && statusCallback != null) {
                    start(sampleCallback, statusCallback)
                }
            }
            CLLocationManager.authorizationStatus() == kCLAuthorizationStatusDenied ||
                CLLocationManager.authorizationStatus() == kCLAuthorizationStatusRestricted -> {
                onStatusChanged?.invoke(SpeedLocationStatus.PermissionMissing)
            }
        }
    }
}

private class IosLocationDelegate(
    private val onLocationUpdated: (CLLocation) -> Unit,
    private val onLocationFailed: () -> Unit,
    private val onAuthorizationChanged: () -> Unit,
) : NSObject(), CLLocationManagerDelegateProtocol {
    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>,
    ) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
        onLocationUpdated(location)
    }

    override fun locationManager(
        manager: CLLocationManager,
        didFailWithError: NSError,
    ) {
        onLocationFailed()
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        onAuthorizationChanged()
    }
}
