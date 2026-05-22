package com.drivepulse.gps

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform