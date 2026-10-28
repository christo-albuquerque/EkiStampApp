package com.ekistamp.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ekistamp.app.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = viewModel::checkForUpdates,
                enabled = state is UpdateState.Idle || state is UpdateState.UpToDate || state is UpdateState.Error,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Check for Updates") }

            when (val s = state) {
                UpdateState.Idle -> Unit
                UpdateState.Checking -> Text("Checking for updates…")
                UpdateState.UpToDate -> Text("You're on the latest version.")
                is UpdateState.Available -> {
                    Text("Version ${s.release.version} is available.", style = MaterialTheme.typography.titleSmall)
                    if (s.release.notes.isNotBlank()) Text(s.release.notes, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { viewModel.downloadAndInstall(s.release) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Download and install")
                    }
                }
                is UpdateState.Downloading -> {
                    Text("Downloading ${s.release.version}…")
                    LinearProgressIndicator(progress = { s.progress }, modifier = Modifier.fillMaxWidth())
                }
                is UpdateState.NeedsPermission -> {
                    Text("Allow Eki Stamps to install updates, then come back and tap Download again.")
                    OutlinedButton(
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Open permission settings") }
                    Button(onClick = { viewModel.downloadAndInstall(s.release) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Download and install")
                    }
                }
                UpdateState.Installing -> Text("Installing… the app will close and need to be reopened.")
                is UpdateState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
