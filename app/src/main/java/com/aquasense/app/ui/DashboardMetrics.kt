package com.aquasense.app.ui

fun hydrationProgress(consumedMl: Int, goalMl: Int = 3_000): Float =
    (consumedMl.toFloat() / goalMl.coerceAtLeast(1)).coerceIn(0f, 1f)

fun filterLifePercent(remainingLiters: Double?, capacityLiters: Double = 1_000.0): Float =
    ((remainingLiters ?: 0.0) / capacityLiters.coerceAtLeast(1.0)).coerceIn(0.0, 1.0).toFloat()

fun tdsQuality(tds: Int?): String = when {
    tds == null -> "Sensor pending"
    tds <= 150 -> "Great quality"
    else -> "Check quality"
}
