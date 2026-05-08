package com.upn.relaxmind.data

import android.content.Context
import com.upn.relaxmind.data.models.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

object AuthManager {
    private const val PREFS_AUTH = "relaxmind_auth"
    private const val KEY_USERS = "registered_users"
    private const val KEY_LAST_USER_EMAIL = "last_user_email"
    private const val KEY_CURRENT_USER_EMAIL = "current_user_email"

    var isSessionUnlocked by mutableStateOf(false)

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)

    fun registerUser(
        context: Context, 
        name: String, 
        email: String, 
        password: String, 
        role: String = "PATIENT",
        phoneNumber: String = "",
        relationship: String? = null
    ): Boolean {
        if (isEmailTaken(context, email)) return false
        
        val users = getRegisteredUsers(context).toMutableList()
        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email.lowercase().trim(),
            password = password,
            role = role,
            phoneNumber = phoneNumber,
            professionalRole = relationship
        )
        users.add(newUser)
        saveUsers(context, users)
        setLastUserEmail(context, email)
        setCurrentUserEmail(context, email)
        return true
    }

    fun loginUser(context: Context, email: String, password: String): User? {
        val users = getRegisteredUsers(context)
        val user = users.find { it.email == email.lowercase().trim() && it.password == password }
        if (user != null) {
            setLastUserEmail(context, user.email)
            setCurrentUserEmail(context, user.email)
            syncPreferences(context, user)
            isSessionUnlocked = true
        }
        return user
    }

    fun loginWithBiometrics(context: Context): User? {
        val lastEmail = getLastUserEmail(context) ?: return null
        val user = getRegisteredUsers(context).find { it.email == lastEmail }
        
        if (user != null && user.biometricEnabled) {
            setCurrentUserEmail(context, user.email)
            syncPreferences(context, user)
            isSessionUnlocked = true
            return user
        }
        return null
    }

    fun isBiometricAvailable(context: Context): Boolean {
        val lastEmail = getLastUserEmail(context) ?: return false
        val user = getRegisteredUsers(context).find { it.email == lastEmail }
        return user?.biometricEnabled ?: false
    }

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        val lastEmail = getLastUserEmail(context) ?: getCurrentUserEmail(context) ?: return
        val users = getRegisteredUsers(context).toMutableList()
        val index = users.indexOfFirst { it.email == lastEmail }
        if (index != -1) {
            users[index] = users[index].copy(biometricEnabled = enabled)
            saveUsers(context, users)
            AppPreferences.setBiometricEnabled(context, enabled)
        }
    }

    fun updateProfile(context: Context, name: String, lastName: String, phoneNumber: String, birthDate: String, condition: String, avatar: String?) {
        val lastEmail = getCurrentUserEmail(context) ?: return
        val users = getRegisteredUsers(context).toMutableList()
        val index = users.indexOfFirst { it.email == lastEmail }
        if (index != -1) {
            users[index] = users[index].copy(
                name = name,
                lastName = lastName,
                phoneNumber = phoneNumber,
                birthDate = birthDate,
                condition = condition,
                avatar = avatar
            )
            saveUsers(context, users)
            syncPreferences(context, users[index])
        }
    }

    /**
     * Links a caregiver with a patient using the patient's ID (or a 6-digit code derived from it).
     */
    // --- Temporary Linking Codes (Yape style) ---
    private const val KEY_TEMP_CODE = "temp_linking_code"
    private const val KEY_TEMP_CODE_TIME = "temp_linking_code_time"
    private const val KEY_TEMP_CODE_USER_ID = "temp_linking_code_user_id"
    private const val EXPIRATION_SECONDS = 120

    fun generateTempCode(context: Context, userId: String): String {
        val code = (100000..999999).random().toString()
        val prefs = getPrefs(context)
        prefs.edit().apply {
            putString(KEY_TEMP_CODE, code)
            putLong(KEY_TEMP_CODE_TIME, System.currentTimeMillis())
            putString(KEY_TEMP_CODE_USER_ID, userId)
            apply()
        }
        return code
    }

    fun validateTempCode(context: Context, code: String): String? {
        val prefs = getPrefs(context)
        val savedCode = prefs.getString(KEY_TEMP_CODE, null)
        val savedTime = prefs.getLong(KEY_TEMP_CODE_TIME, 0)
        val savedUserId = prefs.getString(KEY_TEMP_CODE_USER_ID, null)

        if (savedCode == null || savedUserId == null) return null

        val currentTime = System.currentTimeMillis()
        val diffSeconds = (currentTime - savedTime) / 1000

        if (code == savedCode && diffSeconds <= EXPIRATION_SECONDS) {
            // Valid code, clear it after use
            prefs.edit().remove(KEY_TEMP_CODE).remove(KEY_TEMP_CODE_TIME).remove(KEY_TEMP_CODE_USER_ID).apply()
            return savedUserId
        }
        return null
    }

    fun linkPatient(context: Context, patientIdOrCode: String): Boolean {
        val currentUser = getCurrentUser(context) ?: return false
        if (currentUser.role != "CAREGIVER") return false

        val users = getRegisteredUsers(context).toMutableList()
        
        // Find patient by ID or by a simple 6-digit code (first 6 chars of ID for simplicity in this local demo)
        val patientIndex = users.indexOfFirst { 
            it.id == patientIdOrCode || it.id.take(6).uppercase() == patientIdOrCode.uppercase() 
        }
        
        if (patientIndex == -1 || users[patientIndex].role != "PATIENT") return false
        
        val patientId = users[patientIndex].id
        val caregiverId = currentUser.id

        // Check if already linked
        if (currentUser.linkedUserIds.contains(patientId)) return true

        // Update Caregiver
        val caregiverIndex = users.indexOfFirst { it.id == caregiverId }
        users[caregiverIndex] = users[caregiverIndex].copy(
            linkedUserIds = users[caregiverIndex].linkedUserIds + patientId
        )

        // Update Patient
        users[patientIndex] = users[patientIndex].copy(
            linkedUserIds = users[patientIndex].linkedUserIds + caregiverId
        )

        saveUsers(context, users)
        return true
    }

    fun unlinkUser(context: Context, targetUserId: String): Boolean {
        val currentUser = getCurrentUser(context) ?: return false
        val users = getRegisteredUsers(context).toMutableList()
        
        val userIndex = users.indexOfFirst { it.id == currentUser.id }
        val targetIndex = users.indexOfFirst { it.id == targetUserId }
        
        if (userIndex == -1 || targetIndex == -1) return false
        
        // Update current user
        users[userIndex] = users[userIndex].copy(
            linkedUserIds = users[userIndex].linkedUserIds.filter { it != targetUserId }
        )
        
        // Update target user (bidirectional unlink)
        users[targetIndex] = users[targetIndex].copy(
            linkedUserIds = users[targetIndex].linkedUserIds.filter { it != currentUser.id }
        )
        
        saveUsers(context, users)
        return true
    }

    fun getLinkedUsers(context: Context): List<User> {
        val currentUser = getCurrentUser(context) ?: return emptyList()
        val allUsers = getRegisteredUsers(context)
        return allUsers.filter { currentUser.linkedUserIds.contains(it.id) }
    }

    fun getCurrentUser(context: Context): User? {
        val email = getCurrentUserEmail(context) ?: return null
        return getRegisteredUsers(context).find { it.email == email }
    }

    fun isEmailTaken(context: Context, email: String): Boolean {
        return getRegisteredUsers(context).any { it.email == email.lowercase().trim() }
    }

    fun getRegisteredUsers(context: Context): List<User> {
        val json = getPrefs(context).getString(KEY_USERS, null) ?: return emptyList()
        return try { Json.decodeFromString(json) } catch (e: Exception) { emptyList() }
    }

    fun saveUsers(context: Context, users: List<User>) {
        val json = Json.encodeToString(users)
        getPrefs(context).edit().putString(KEY_USERS, json).apply()
    }

    private fun setLastUserEmail(context: Context, email: String?) {
        getPrefs(context).edit().putString(KEY_LAST_USER_EMAIL, email).apply()
    }

    fun getLastUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_LAST_USER_EMAIL, null)
    }

    private fun setCurrentUserEmail(context: Context, email: String?) {
        getPrefs(context).edit().putString(KEY_CURRENT_USER_EMAIL, email).apply()
    }

    fun getCurrentUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_CURRENT_USER_EMAIL, null)
    }

    fun updateRelationship(context: Context, patientId: String, newRelationship: String): Boolean {
        val currentUser = getCurrentUser(context) ?: return false
        val users = getRegisteredUsers(context).toMutableList()
        val index = users.indexOfFirst { it.id == currentUser.id }
        if (index != -1) {
            val updatedMap = users[index].patientRelationships.toMutableMap()
            updatedMap[patientId] = newRelationship
            users[index] = users[index].copy(patientRelationships = updatedMap)
            saveUsers(context, users)
            return true
        }
        return false
    }

    private fun syncPreferences(context: Context, user: User) {
        AppPreferences.saveDisplayName(context, user.name)
        AppPreferences.saveBirthDate(context, user.birthDate)
        AppPreferences.saveCondition(context, user.condition)
        AppPreferences.setBiometricEnabled(context, user.biometricEnabled)
    }

    fun logout(context: Context) {
        AppPreferences.clear(context)
        setCurrentUserEmail(context, null)
        isSessionUnlocked = false
    }
}
