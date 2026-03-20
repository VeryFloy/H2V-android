package com.h2v.messenger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val H2VColorScheme = darkColorScheme(
    primary = H2VColors.AccentBlue,
    onPrimary = H2VColors.TextOnAccent,
    primaryContainer = H2VColors.AccentBlueDim,
    onPrimaryContainer = H2VColors.AccentBlueLight,
    secondary = H2VColors.AccentBlueSoft,
    onSecondary = H2VColors.TextOnAccent,
    background = H2VColors.Background,
    onBackground = H2VColors.TextPrimary,
    surface = H2VColors.Surface,
    onSurface = H2VColors.TextPrimary,
    surfaceVariant = H2VColors.SurfaceGlass,
    onSurfaceVariant = H2VColors.TextSecondary,
    outline = H2VColors.GlassBorder,
    outlineVariant = H2VColors.Divider,
    error = H2VColors.Error,
    onError = H2VColors.TextOnAccent
)

@Composable
fun LiquidGlassTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = H2VColorScheme,
        typography = H2VTypography,
        shapes = H2VShapes,
        content = content
    )
}
