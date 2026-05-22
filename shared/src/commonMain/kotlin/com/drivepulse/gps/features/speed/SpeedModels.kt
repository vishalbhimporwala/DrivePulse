package com.drivepulse.gps.features.speed

import kotlin.math.roundToInt

enum class SpeedUnit(
    val shortLabel: String,
    private val metersPerSecondMultiplier: Double,
) {
    KilometersPerHour("KM/H", 3.6),
    MilesPerHour("MPH", 2.2369362921),
    ;

    fun fromMetersPerSecond(value: Double): Int =
        (value * metersPerSecondMultiplier).roundToInt().coerceAtLeast(0)
}

data class SpeedReading(
    val currentMetersPerSecond: Double = 0.0,
    val maxMetersPerSecond: Double = 0.0,
    val averageMetersPerSecond: Double = 0.0,
    val accuracyMeters: Double? = null,
    val isGpsLocked: Boolean = false,
    val unit: SpeedUnit = SpeedUnit.KilometersPerHour,
) {
    val currentSpeed: Int = unit.fromMetersPerSecond(currentMetersPerSecond)
    val maxSpeed: Int = unit.fromMetersPerSecond(maxMetersPerSecond)
    val averageSpeed: Int = unit.fromMetersPerSecond(averageMetersPerSecond)

    val accuracyLabel: String =
        accuracyMeters?.let { "${it.roundToInt()} m" } ?: "--"

    val gpsStatusLabel: String =
        if (isGpsLocked) "GPS locked" else "Waiting for GPS"
}

data class SpeedSessionState(
    val currentMetersPerSecond: Double = 0.0,
    val rawMetersPerSecond: Double = 0.0,
    val maxMetersPerSecond: Double = 0.0,
    val averageMetersPerSecond: Double = 0.0,
    val accuracyMeters: Double? = null,
    val sampleCount: Int = 0,
    val status: SpeedLocationStatus = SpeedLocationStatus.PermissionMissing,
    val unit: SpeedUnit = SpeedUnit.KilometersPerHour,
) {
    val reading: SpeedReading =
        SpeedReading(
            currentMetersPerSecond = currentMetersPerSecond,
            maxMetersPerSecond = maxMetersPerSecond,
            averageMetersPerSecond = averageMetersPerSecond,
            accuracyMeters = accuracyMeters,
            isGpsLocked = status == SpeedLocationStatus.GpsLocked,
            unit = unit,
        )

    fun withSample(sample: SpeedLocationSample): SpeedSessionState {
        val nextCount = sampleCount + 1
        val smoothedSpeed = if (sampleCount == 0) {
            sample.speedMetersPerSecond
        } else {
            (currentMetersPerSecond * SmoothingWeight) + (sample.speedMetersPerSecond * (1.0 - SmoothingWeight))
        }
        val nextAverage = if (sampleCount == 0) {
            smoothedSpeed
        } else {
            ((averageMetersPerSecond * sampleCount) + smoothedSpeed) / nextCount
        }

        return copy(
            currentMetersPerSecond = smoothedSpeed,
            rawMetersPerSecond = sample.speedMetersPerSecond,
            maxMetersPerSecond = maxOf(maxMetersPerSecond, smoothedSpeed),
            averageMetersPerSecond = nextAverage,
            accuracyMeters = sample.accuracyMeters,
            sampleCount = nextCount,
            status = SpeedLocationStatus.GpsLocked,
        )
    }

    fun withUnit(nextUnit: SpeedUnit): SpeedSessionState =
        copy(unit = nextUnit)

    val smoothingLabel: String =
        if (sampleCount > 1) "Active" else "Ready"

    private companion object {
        const val SmoothingWeight = 0.68
    }
}
