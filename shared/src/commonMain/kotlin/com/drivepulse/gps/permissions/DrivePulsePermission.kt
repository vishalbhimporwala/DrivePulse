package com.drivepulse.gps.permissions

enum class DrivePulsePermission(
    val title: String,
    val rationale: String,
    val requiredForLaunch: Boolean,
) {
    LocationWhenInUse(
        title = "Location access",
        rationale = "Used for current location, traffic context, GPS speed, and active trip recording.",
        requiredForLaunch = true,
    ),
    PreciseLocation(
        title = "Precise location",
        rationale = "Improves speed accuracy, distance calculation, and route quality.",
        requiredForLaunch = true,
    ),
    BackgroundLocation(
        title = "Background location",
        rationale = "Used only during active trip recording so routes continue when DrivePulse is not open.",
        requiredForLaunch = false,
    ),
    Notifications(
        title = "Notifications",
        rationale = "Used for foreground trip recording status and future speed alerts.",
        requiredForLaunch = false,
    ),
}

enum class PermissionGrantState {
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied,
}

data class PermissionUiState(
    val permission: DrivePulsePermission,
    val grantState: PermissionGrantState = PermissionGrantState.Unknown,
) {
    val canRequest: Boolean =
        grantState == PermissionGrantState.Unknown || grantState == PermissionGrantState.Denied
}

object DrivePulsePermissionPlan {
    val launchPermissions = listOf(
        DrivePulsePermission.LocationWhenInUse,
        DrivePulsePermission.PreciseLocation,
    )

    val tripRecordingPermissions = listOf(
        DrivePulsePermission.BackgroundLocation,
        DrivePulsePermission.Notifications,
    )
}
