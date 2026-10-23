package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FitAdminLightScheme = lightColorScheme(
    primary = Charcoal900,
    onPrimary = Color.White,
    primaryContainer = Charcoal800,
    onPrimaryContainer = ElectricLime,
    secondary = ElectricLime,
    onSecondary = Charcoal900,
    secondaryContainer = ElectricLimeLight,
    onSecondaryContainer = ElectricLimeDark,
    tertiary = AlertRed,
    onTertiary = Color.White,
    tertiaryContainer = AlertRedLight,
    onTertiaryContainer = AlertRedDark,
    background = FitBackground,
    onBackground = FitTextPrimary,
    surface = FitSurface,
    onSurface = FitTextPrimary,
    surfaceVariant = FitSurfaceVariant,
    onSurfaceVariant = FitTextSecondary,
    outline = FitBorder,
    error = AlertRed,
    onError = Color.White
)

private val FitAdminDarkScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = Charcoal900,
    primaryContainer = Charcoal800,
    onPrimaryContainer = ElectricLime,
    secondary = ElectricLime,
    onSecondary = Charcoal900,
    secondaryContainer = Charcoal700,
    onSecondaryContainer = ElectricLimeLight,
    tertiary = AlertRed,
    onTertiary = Color.White,
    tertiaryContainer = Charcoal800,
    onTertiaryContainer = AlertRedLight,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Charcoal900,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Charcoal800,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Charcoal700,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to strictly preserve the user-requested bold black & electric lime palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FitAdminDarkScheme else FitAdminLightScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Charcoal900.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
