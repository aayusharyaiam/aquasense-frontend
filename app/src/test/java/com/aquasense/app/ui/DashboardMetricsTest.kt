package com.aquasense.app.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardMetricsTest {
    @Test
    fun hydrationProgress_isClampedToDailyGoal() {
        assertEquals(0.5f, hydrationProgress(1_500), 0.001f)
        assertEquals(1f, hydrationProgress(4_000), 0.001f)
        assertEquals(0f, hydrationProgress(-100), 0.001f)
    }

    @Test
    fun filterLifePercent_handlesMissingAndOverflowingValues() {
        assertEquals(0f, filterLifePercent(null), 0.001f)
        assertEquals(0.742f, filterLifePercent(742.0), 0.001f)
        assertEquals(1f, filterLifePercent(1_500.0), 0.001f)
        assertTrue(filterLifePercent(-10.0) >= 0f)
    }

    @Test
    fun tdsQuality_explainsSensorBoundaries() {
        assertEquals("Sensor pending", tdsQuality(null))
        assertEquals("Great quality", tdsQuality(150))
        assertEquals("Check quality", tdsQuality(151))
    }
}
