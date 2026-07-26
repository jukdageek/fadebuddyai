package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DeepNavy,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = CyanAccent,
    secondary = CopperAccent,
    onSecondary = DeepNavy,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = CopperAccent,
    tertiary = CueVioletPairing,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = DarkNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderNavy,
    error = CueRedFault,
    onError = TextPrimary
)

private val LightColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = DeepNavy,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = CyanAccent,
    secondary = CopperAccent,
    onSecondary = DeepNavy,
    tertiary = CueVioletPairing,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = DarkNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderNavy
)

@Composable
fun FadeBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Default to dark navy titanium design

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
