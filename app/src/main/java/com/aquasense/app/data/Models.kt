package com.aquasense.app.data

data class Device(
    val id: String,
    val ownerId: String?,
    val name: String,
    val status: String,
    val tankLevelMl: Int?,
    val currentTds: Int?,
    val filterLifeLiters: Double?,
    val firmwareVersion: String?,
    val lastSeenAt: String?,
    val role: String,
)

data class DashboardSnapshot(
    val deviceId: String,
    val name: String,
    val status: String,
    val tankLevelMl: Int?,
    val currentTds: Int?,
    val ph: Double?,
    val filterLifeLiters: Double?,
    val firmwareVersion: String?,
    val lastSeenAt: String?,
    val waterConsumedTodayMl: Int,
    val waterConsumedWeekMl: Int,
    val sessionCountToday: Int,
)

data class DispenseSession(
    val id: String,
    val deviceId: String,
    val userId: String?,
    val volumeMl: Int,
    val avgTdsOut: Int?,
    val temperatureC: Double?,
    val startTs: String,
    val endTs: String,
)

data class CommandResult(val commandId: String, val status: String)

data class AuthResult(
    val accessToken: String,
    val userId: String,
    val displayName: String,
)

data class AppUser(
    val displayName: String,
    val userId: String,
    val demoMode: Boolean,
)

class ApiException(message: String) : Exception(message)
