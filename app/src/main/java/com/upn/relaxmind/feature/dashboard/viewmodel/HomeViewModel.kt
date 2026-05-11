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
import com.upn.relaxmind.core.data.database.AppDatabase
import com.upn.relaxmind.core.data.database.PatientCaregiverLinkEntity
import com.upn.relaxmind.core.data.sync.CaregiverLinkRepository

data class HomeUiState(
    val displayName: String = "",
    val streak: Int = 0,
    val wellnessScore: Int = 0,
    val role: String = "patient",
    val showHeader: Boolean = false,
    val showScore: Boolean = false,
    val showGrid: Boolean = false,
    val showWidgets: Boolean = false,
    val isLoading: Boolean = true,
    val linkedPatients: List<PatientCaregiverLinkEntity> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val db = AppDatabase.getDatabase(application)
    private val linkRepository = CaregiverLinkRepository(
        linkDao = db.patientCaregiverLinkDao(),
        profileDao = db.profileDao(),
        syncQueueDao = db.syncQueueDao(),
        context = application
    )

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
                role = user?.role ?: "patient",
                isLoading = false
            )}
            
            val userId = user?.id ?: "local_user"
            com.upn.relaxmind.core.data.sync.CloudMigrationManager.migrateIfNecessary(context, userId)

            // Si es cuidador, cargar la lista de pacientes
            if (user?.role == "caregiver") {
                linkRepository.getLinkedPatients(userId).collect { patients ->
                    _uiState.update { it.copy(linkedPatients = patients) }
                }
            }
            
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
