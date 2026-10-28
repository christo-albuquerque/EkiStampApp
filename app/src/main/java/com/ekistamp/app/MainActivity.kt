package com.ekistamp.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ekistamp.app.ui.catalog.CatalogScreen
import com.ekistamp.app.ui.catalog.CatalogViewModel
import com.ekistamp.app.ui.detail.DetailScreen
import com.ekistamp.app.ui.detail.DetailViewModel
import com.ekistamp.app.ui.settings.SettingsScreen
import com.ekistamp.app.ui.settings.SettingsViewModel
import com.ekistamp.app.ui.theme.EkiStampTheme
import com.ekistamp.app.ui.theme.GlassBackground
import com.ekistamp.app.ui.theme.isDark

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as EkiStampApplication
        setContent {
            val theme by app.themePreferences.settings.collectAsStateWithLifecycle()
            val dark = theme.isDark(isSystemInDarkTheme())

            // Keep status/navigation bar icons readable against the app's own theme, not the system's.
            DisposableEffect(dark) {
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose {}
            }

            EkiStampTheme(theme) {
                GlassBackground { AppNavigation(app) }
            }
        }
    }
}

@Composable
private fun AppNavigation(app: EkiStampApplication) {
    val repository = app.repository
    val navController = rememberNavController()

    NavHost(navController, startDestination = "catalog") {
        composable("catalog") {
            val vm: CatalogViewModel = viewModel(
                factory = viewModelFactory { initializer { CatalogViewModel(repository) } }
            )
            CatalogScreen(
                vm,
                onStampClick = { id -> navController.navigate("detail/${android.net.Uri.encode(id)}") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            val vm: SettingsViewModel = viewModel(
                factory = viewModelFactory { initializer { SettingsViewModel(app.updateChecker, app.apkInstaller) } }
            )
            val theme by app.themePreferences.settings.collectAsStateWithLifecycle()
            SettingsScreen(
                viewModel = vm,
                themeSettings = theme,
                onModeChange = app.themePreferences::setMode,
                onAccentChange = app.themePreferences::setAccent,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            val vm: DetailViewModel = viewModel(
                factory = viewModelFactory { initializer { DetailViewModel(repository, id) } }
            )
            DetailScreen(vm, onBack = { navController.popBackStack() })
        }
    }
}
