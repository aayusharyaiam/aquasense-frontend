package com.aquasense.app.data

object DemoData {
    const val deviceId = "demo-device"

    fun dashboard() = DashboardSnapshot(
        deviceId = deviceId,
        name = "AquaSense One",
        status = "online",
        tankLevelMl = 3600,
        currentTds = 86,
        ph = 7.2,
        filterLifeLiters = 742.0,
        firmwareVersion = "0.9.4-edge",
        lastSeenAt = "just now",
        waterConsumedTodayMl = 2410,
        waterConsumedWeekMl = 14800,
        sessionCountToday = 8,
    )

    fun sessions() = listOf(
        DispenseSession("demo-1", deviceId, "you", 420, 84, 23.4, "Today, 09:12", "Today, 09:14"),
        DispenseSession("demo-2", deviceId, "krish", 280, 91, 23.5, "Today, 08:44", "Today, 08:45"),
        DispenseSession("demo-3", deviceId, null, 350, 88, 23.2, "Yesterday, 19:20", "Yesterday, 19:21"),
    )
}
