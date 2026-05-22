package com.drivepulse.gps.features.speed

interface SpeedLocationProvider {
    val hasLocationPermission: Boolean

    fun requestLocationPermission()

    fun start(
        onSample: (SpeedLocationSample) -> Unit,
        onStatusChanged: (SpeedLocationStatus) -> Unit,
    )

    fun stop()
}

data class SpeedLocationSample(
    val speedMetersPerSecond: Double,
    val accuracyMeters: Double?,
    val timestampMillis: Long,
)

enum class SpeedLocationStatus {
    PermissionMissing,
    Searching,
    GpsLocked,
    ProviderDisabled,
    Unavailable,
}

object PreviewSpeedLocationProvider : SpeedLocationProvider {
    override val hasLocationPermission: Boolean = false

    override fun requestLocationPermission() = Unit

    override fun start(
        onSample: (SpeedLocationSample) -> Unit,
        onStatusChanged: (SpeedLocationStatus) -> Unit,
    ) {
        onStatusChanged(SpeedLocationStatus.PermissionMissing)
    }

    override fun stop() = Unit
}
