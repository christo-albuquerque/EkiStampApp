package com.ekistamp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFFC4607A),
    secondary = Color(0xFFE8849A),
    background = Color(0xFFFFF0F4)
)
private val Dark = darkColorScheme(
    primary = Color(0xFFF5B8CA),
    secondary = Color(0xFFE8849A)
)

@Composable
fun EkiStampTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        content = content
    )
}
