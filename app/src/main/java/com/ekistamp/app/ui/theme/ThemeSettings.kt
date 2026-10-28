package com.ekistamp.app.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeMode(val label: String) {
    SYSTEM("System"), LIGHT("Light"), DARK("Dark")
}

/** 16 selectable accent colors, tuned after the iOS system palette. */
enum class Accent(val label: String, val color: Color) {
    BLUE("Blue", Color(0xFF0A84FF)),
    INDIGO("Indigo", Color(0xFF5E5CE6)),
    PURPLE("Purple", Color(0xFFBF5AF2)),
    MAGENTA("Magenta", Color(0xFFE040FB)),
    PINK("Pink", Color(0xFFFF375F)),
    SAKURA("Sakura", Color(0xFFF48FB1)),
    RED("Red", Color(0xFFFF453A)),
    ORANGE("Orange", Color(0xFFFF9F0A)),
    YELLOW("Yellow", Color(0xFFFFD60A)),
    LIME("Lime", Color(0xFFA6E22E)),
    GREEN("Green", Color(0xFF30D158)),
    MINT("Mint", Color(0xFF63E6C2)),
    TEAL("Teal", Color(0xFF40C8E0)),
    CYAN("Cyan", Color(0xFF64D2FF)),
    BROWN("Brown", Color(0xFFAC8E68)),
    GRAPHITE("Graphite", Color(0xFF8E8E93))
}

data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val accent: Accent = Accent.BLUE
)
