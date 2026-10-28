package com.ekistamp.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekistamp.app.data.update.ApkInstaller
import com.ekistamp.app.data.update.ReleaseInfo
import com.ekistamp.app.data.update.UpdateCheckResult
import com.ekistamp.app.data.update.UpdateChecker
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data object UpToDate : UpdateState
    data class Available(val release: ReleaseInfo) : UpdateState
    data class Downloading(val release: ReleaseInfo, val progress: Float) : UpdateState
    data class NeedsPermission(val release: ReleaseInfo) : UpdateState
    data object Installing : UpdateState
    data class Error(val message: String) : UpdateState
}

class SettingsViewModel(
    private val checker: UpdateChecker,
    private val installer: ApkInstaller
) : ViewModel() {

    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state

    fun checkForUpdates() {
        _state.value = UpdateState.Checking
        viewModelScope.launch {
            _state.value = runCatching { checker.check() }.fold(
                onSuccess = {
                    when (it) {
                        UpdateCheckResult.UpToDate -> UpdateState.UpToDate
                        is UpdateCheckResult.Available -> UpdateState.Available(it.release)
                    }
                },
                onFailure = { UpdateState.Error(it.message ?: "Couldn't check for updates.") }
            )
        }
    }

    fun downloadAndInstall(release: ReleaseInfo) {
        if (!installer.canInstall()) {
            _state.value = UpdateState.NeedsPermission(release)
            return
        }
        viewModelScope.launch {
            _state.value = UpdateState.Downloading(release, 0f)
            runCatching {
                val apk: File = installer.download(release.apkUrl) {
                    _state.value = UpdateState.Downloading(release, it)
                }
                _state.value = UpdateState.Installing
                installer.install(apk)
            }.onFailure {
                _state.value = UpdateState.Error(it.message ?: "Update failed.")
            }
        }
    }
}
