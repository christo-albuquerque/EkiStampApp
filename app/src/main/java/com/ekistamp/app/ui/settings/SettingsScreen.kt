package com.ekistamp.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ekistamp.app.BuildConfig
import com.ekistamp.app.ui.theme.Accent
import com.ekistamp.app.ui.theme.GlassCard
import com.ekistamp.app.ui.theme.ThemeMode
import com.ekistamp.app.ui.theme.ThemeSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    themeSettings: ThemeSettings,
    onModeChange: (ThemeMode) -> Unit,
    onAccentChange: (Accent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 16.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppearanceSection(themeSettings, onModeChange, onAccentChange)
            UpdatesSection(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AppearanceSection(
    settings: ThemeSettings,
    onModeChange: (ThemeMode) -> Unit,
    onAccentChange: (Accent) -> Unit
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = settings.mode == mode,
                        onClick = { onModeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                        label = { Text(mode.label) }
                    )
                }
            }

            Text("Accent color", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Accent.entries.forEach { accent ->
                    AccentSwatch(accent, selected = accent == settings.accent, onClick = { onAccentChange(accent) })
                }
            }
        }
    }
}

@Composable
private fun AccentSwatch(accent: Accent, selected: Boolean, onClick: () -> Unit) {
    val onAccent = if (accent.color.luminance() > 0.5f) Color(0xFF1C1C1E) else Color.White
    Box(
        Modifier
            .size(40.dp)
            .background(accent.color, CircleShape)
            .border(if (selected) 3.dp else 1.dp, if (selected) Color.White else Color.White.copy(alpha = 0.5f), CircleShape)
            .semantics {
                contentDescription = accent.label
                this.selected = selected
            }
            .clickable(role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) Icon(Icons.Default.Check, contentDescription = null, tint = onAccent, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun UpdatesSection(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    GlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)

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
