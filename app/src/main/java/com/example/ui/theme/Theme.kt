package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FitPulseDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = PulseBackground,
    primaryContainer = PulseSurfaceVariant,
    onPrimaryContainer = ElectricCyan,
    secondary = NeonLime,
    onSecondary = PulseBackground,
    secondaryContainer = PulseSurfaceElevated,
    onSecondaryContainer = NeonLime,
    tertiary = NeonOrange,
    onTertiary = PulseBackground,
    background = PulseBackground,
    onBackground = PulseTextPrimary,
    surface = PulseSurface,
    onSurface = PulseTextPrimary,
    surfaceVariant = PulseSurfaceVariant,
    onSurfaceVariant = PulseTextSecondary,
    outline = PulseBorder,
    error = PulseRed,
    onError = PulseTextPrimary
)

private val FitPulseLightColorScheme = lightColorScheme(
    primary = ElectricCyanDark,
    onPrimary = PulseTextPrimary,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = ElectricCyanDark,
    secondary = NeonLime,
    onSecondary = PulseBackground,
    tertiary = NeonOrange,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = PulseRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // FitPulse prioritizes athletic dark theme
    dynamicColor: Boolean = false, // Keep high-energy brand colors for consistency
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FitPulseDarkColorScheme else FitPulseLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FitPulseTypography,
        content = content
    )
}
