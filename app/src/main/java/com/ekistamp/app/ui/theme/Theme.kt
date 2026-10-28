package com.ekistamp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

val LocalDarkTheme = staticCompositionLocalOf { false }

fun ThemeSettings.isDark(systemDark: Boolean): Boolean = when (mode) {
    ThemeMode.SYSTEM -> systemDark
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun EkiStampTheme(settings: ThemeSettings, content: @Composable () -> Unit) {
    val dark = settings.isDark(isSystemInDarkTheme())
    val scheme = remember(dark, settings.accent) { buildScheme(dark, settings.accent.color) }
    CompositionLocalProvider(LocalDarkTheme provides dark) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

private fun buildScheme(dark: Boolean, accent: Color): ColorScheme {
    val onAccent = if (accent.luminance() > 0.5f) Color(0xFF1C1C1E) else Color.White
    return if (dark) {
        darkColorScheme(
            primary = accent,
            onPrimary = onAccent,
            secondary = accent,
            onSecondary = onAccent,
            secondaryContainer = lerp(Color(0xFF1C1C22), accent, 0.35f),
            onSecondaryContainer = Color(0xFFF2F2F7),
            background = lerp(Color(0xFF0A0A0F), accent, 0.10f),
            onBackground = Color(0xFFF2F2F7),
            surface = lerp(Color(0xFF0A0A0F), accent, 0.10f),
            onSurface = Color(0xFFF2F2F7),
            onSurfaceVariant = Color(0xFFA1A1A8),
            outline = Color(0x33FFFFFF)
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = onAccent,
            secondary = accent,
            onSecondary = onAccent,
            secondaryContainer = lerp(Color.White, accent, 0.25f),
            onSecondaryContainer = Color(0xFF1C1C1E),
            background = lerp(Color(0xFFF7F7FB), accent, 0.10f),
            onBackground = Color(0xFF1C1C1E),
            surface = lerp(Color(0xFFF7F7FB), accent, 0.10f),
            onSurface = Color(0xFF1C1C1E),
            onSurfaceVariant = Color(0xFF6C6C70),
            outline = Color(0x33000000)
        )
    }
}
