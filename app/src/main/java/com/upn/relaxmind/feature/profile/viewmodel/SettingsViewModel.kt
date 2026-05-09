package com.upn.relaxmind.feature.profile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.core.data.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val biometricEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val healthConnectEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val showFeedbackDialog: Boolean = false,
    val isLoading: Boolean = true
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val context = getApplication<Application>().applicationContext
        _uiState.update { it.copy(
            biometricEnabled = AppPreferences.isBiometricEnabled(context),
            isLoading = false
        )}
    }

    fun toggleBiometric(enabled: Boolean) {
        val context = getApplication<Application>().applicationContext
        AppPreferences.setBiometricEnabled(context, enabled)
        AuthManager.setBiometricEnabled(context, enabled)
        _uiState.update { it.copy(biometricEnabled = enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleHealthConnect(enabled: Boolean) {
        _uiState.update { it.copy(healthConnectEnabled = enabled) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    fun showFeedbackDialog(show: Boolean) {
        _uiState.update { it.copy(showFeedbackDialog = show) }
    }
}
