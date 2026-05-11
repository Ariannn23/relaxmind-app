package com.upn.relaxmind.feature.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.database.AppDatabase
import com.upn.relaxmind.core.data.database.PatientCaregiverLinkEntity
import com.upn.relaxmind.core.data.sync.CaregiverLinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LinkUiState(
    val generatedCode: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val pendingRequests: List<PatientCaregiverLinkEntity> = emptyList()
)

class LinkViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(LinkUiState())
    val uiState = _uiState.asStateFlow()

    private val db = AppDatabase.getDatabase(application)
    // En una app real usaríamos inyección de dependencias (Hilt) para este repositorio
    // Aquí lo instanciamos manualmente para simplificar la integración sin tocar la configuración de Hilt.
    private val linkRepository = CaregiverLinkRepository(
        linkDao = db.patientCaregiverLinkDao(),
        profileDao = db.profileDao(),
        syncQueueDao = db.syncQueueDao(),
        context = application
    )

    fun observePendingRequests() {
        viewModelScope.launch {
            val user = AuthManager.getCurrentUser(getApplication())
            val patientId = user?.id ?: return@launch
            
            linkRepository.getPendingRequestsForPatient(patientId).collect { requests ->
                _uiState.update { it.copy(pendingRequests = requests) }
            }
        }
    }

    fun generateCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = AuthManager.getCurrentUser(getApplication())
                val patientId = user?.id ?: throw Exception("Usuario no autenticado")
                
                val code = linkRepository.generateTempCode(patientId)
                _uiState.update { it.copy(generatedCode = code, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun validateCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val user = AuthManager.getCurrentUser(getApplication())
                val caregiverId = user?.id ?: throw Exception("Usuario no autenticado")
                
                val result = linkRepository.validateAndSendLink(code, caregiverId)
                if (result.isSuccess) {
                    _uiState.update { it.copy(successMessage = result.getOrNull(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = result.exceptionOrNull()?.message, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun acceptRequest(linkId: String) {
        viewModelScope.launch {
            try {
                val user = AuthManager.getCurrentUser(getApplication())
                val patientId = user?.id ?: return@launch
                linkRepository.acceptLink(linkId, patientId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
