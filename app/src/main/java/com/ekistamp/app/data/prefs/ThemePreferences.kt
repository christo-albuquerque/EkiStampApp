package com.ekistamp.app.data.prefs

import android.content.Context
import com.ekistamp.app.ui.theme.Accent
import com.ekistamp.app.ui.theme.ThemeMode
import com.ekistamp.app.ui.theme.ThemeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ThemePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("theme", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        ThemeSettings(
            mode = enumOrDefault(prefs.getString(KEY_MODE, null), ThemeMode.SYSTEM),
            accent = enumOrDefault(prefs.getString(KEY_ACCENT, null), Accent.BLUE)
        )
    )
    val settings: StateFlow<ThemeSettings> = _settings

    fun setMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_MODE, mode.name).apply()
        _settings.update { it.copy(mode = mode) }
    }

    fun setAccent(accent: Accent) {
        prefs.edit().putString(KEY_ACCENT, accent.name).apply()
        _settings.update { it.copy(accent = accent) }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T =
        enumValues<T>().firstOrNull { it.name == name } ?: default

    private companion object {
        const val KEY_MODE = "mode"
        const val KEY_ACCENT = "accent"
    }
}
