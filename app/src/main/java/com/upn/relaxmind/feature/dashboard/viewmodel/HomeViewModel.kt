package com.upn.relaxmind.feature.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.preferences.AppPreferences
import com.upn.relaxmind.feature.gamification.data.GamificationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val displayName: String = "",
    val streak: Int = 0,
    val wellnessScore: Int = 0,
    val showHeader: Boolean = false,
    val showScore: Boolean = false,
    val showGrid: Boolean = false,
    val showWidgets: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val user = AuthManager.getCurrentUser(context)
            
            _uiState.update { it.copy(
                displayName = user?.name ?: AppPreferences.getDisplayName(context),
                streak = user?.streakCount ?: AppPreferences.getStreak(context),
                wellnessScore = user?.wellnessScore ?: AppPreferences.getWellnessScore(context),
                isLoading = false
            )}
            
            GamificationManager.updateActivity(context)
            startAnimations()
        }
    }

    private fun startAnimations() {
        viewModelScope.launch {
            delay(50)
            _uiState.update { it.copy(showHeader = true) }
            delay(90)
            _uiState.update { it.copy(showScore = true) }
            delay(90)
            _uiState.update { it.copy(showGrid = true) }
            delay(90)
            _uiState.update { it.copy(showWidgets = true) }
        }
    }
}
