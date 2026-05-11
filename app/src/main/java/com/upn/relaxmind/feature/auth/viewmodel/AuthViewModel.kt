package com.upn.relaxmind.feature.auth.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.upn.relaxmind.R
import com.upn.relaxmind.core.data.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.fragment.app.FragmentActivity

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGoogleLoginSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onGoogleSignIn(context: Context, activity: FragmentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            
            val credentialManager = CredentialManager.create(context)
            val webClientId = context.getString(R.string.default_web_client_id)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    context = activity,
                    request = request
                )
                handleGoogleSignInResult(context, result)
            } catch (e: GetCredentialException) {
                _uiState.value = AuthUiState(error = e.message ?: "Error al iniciar sesión con Google")
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Ocurrió un error inesperado")
            }
        }
    }

    private fun handleGoogleSignInResult(context: Context, result: GetCredentialResponse) {
        val credential = result.credential
        
        // Use GoogleIdTokenCredential.createFrom to parse the data
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val email = googleIdTokenCredential.id
            val name = googleIdTokenCredential.displayName ?: "Usuario Google"
            val avatar = googleIdTokenCredential.profilePictureUri?.toString()
            val googleId = googleIdTokenCredential.id

            AuthManager.loginOrRegisterGoogleUser(
                context = context,
                id = googleId,
                name = name,
                email = email,
                avatar = avatar
            )
            
            _uiState.value = AuthUiState(isGoogleLoginSuccess = true)
        } catch (e: Exception) {
            _uiState.value = AuthUiState(error = "Error al procesar credencial de Google: ${e.message}")
        }
    }
    
    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isGoogleLoginSuccess = false)
    }
}
