package com.upn.relaxmind.feature.progress.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upn.relaxmind.core.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProgressUiState(
    val displayName: String = "",
    val streak: Int = 0,
    val wellnessScore: Int = 0,
    val selectedRange: String = "7 días",
    val selectedMonth: String = "Abril",
    val isLoading: Boolean = true
)

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            _uiState.update { it.copy(
                displayName = AppPreferences.getDisplayName(context),
                streak = AppPreferences.getStreak(context),
                wellnessScore = AppPreferences.getWellnessScore(context),
                isLoading = false
            )}
        }
    }

    fun onRangeSelected(range: String) {
        _uiState.update { it.copy(selectedRange = range) }
    }

    fun onMonthSelected(month: String) {
        _uiState.update { it.copy(selectedMonth = month) }
    }
}
