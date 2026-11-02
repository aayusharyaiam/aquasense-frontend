package com.aquasense.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AquaSenseColors = lightColorScheme(
    primary = Teal,
    onPrimary = Card,
    secondary = Aqua,
    onSecondary = TealDeep,
    tertiary = Amber,
    background = Canvas,
    onBackground = Ink,
    surface = Card,
    onSurface = Ink,
    surfaceVariant = AquaSoft,
    onSurfaceVariant = InkMuted,
    outline = Line,
    error = Coral,
)

private val AquaSenseTypography = Typography().copy(
    headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.8).sp),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = Typography().bodyLarge.copy(lineHeight = 25.sp),
    labelLarge = Typography().labelLarge.copy(fontWeight = FontWeight.Bold),
)

@Composable
fun AquaSenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AquaSenseColors,
        typography = AquaSenseTypography,
        content = content,
    )
}
