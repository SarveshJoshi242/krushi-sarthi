package com.krushiadhaar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KrushiColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    background = AppBackground,
    surface = AppSurface,
    onPrimary = AppOnPrimary,
    onBackground = AppOnBackground,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppOnSurfaceVariant
)

@Composable
fun KrushiAdhaarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KrushiColorScheme,
        content = content
    )
}
