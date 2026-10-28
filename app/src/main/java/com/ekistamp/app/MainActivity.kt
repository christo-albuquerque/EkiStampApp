package com.ekistamp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ekistamp.app.ui.settings.SettingsScreen
import com.ekistamp.app.ui.settings.SettingsViewModel
import com.ekistamp.app.ui.catalog.CatalogScreen
import com.ekistamp.app.ui.catalog.CatalogViewModel
import com.ekistamp.app.ui.detail.DetailScreen
import com.ekistamp.app.ui.detail.DetailViewModel
import com.ekistamp.app.ui.theme.EkiStampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as EkiStampApplication
        setContent {
            EkiStampTheme { AppNavigation(app) }
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
            SettingsScreen(vm, onBack = { navController.popBackStack() })
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
