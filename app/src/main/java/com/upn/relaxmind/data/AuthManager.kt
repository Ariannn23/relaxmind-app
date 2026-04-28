package com.upn.relaxmind.data

import android.content.Context
import com.upn.relaxmind.data.models.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

object AuthManager {
    private const val PREFS_AUTH = "relaxmind_auth"
    private const val KEY_USERS = "registered_users"
    private const val KEY_LAST_USER_EMAIL = "last_user_email"
    private const val KEY_CURRENT_USER_EMAIL = "current_user_email"

    var isSessionUnlocked = false

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)

    fun registerUser(context: Context, name: String, email: String, password: String): Boolean {
        if (isEmailTaken(context, email)) return false
        
        val users = getRegisteredUsers(context).toMutableList()
        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email.lowercase().trim(),
            password = password
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

    fun updateProfile(context: Context, name: String, lastName: String, birthDate: String, condition: String) {
        val lastEmail = getCurrentUserEmail(context) ?: return
        val users = getRegisteredUsers(context).toMutableList()
        val index = users.indexOfFirst { it.email == lastEmail }
        if (index != -1) {
            users[index] = users[index].copy(
                name = name,
                lastName = lastName,
                birthDate = birthDate,
                condition = condition
            )
            saveUsers(context, users)
            syncPreferences(context, users[index])
        }
    }

    fun getCurrentUser(context: Context): User? {
        val email = getCurrentUserEmail(context) ?: return null
        return getRegisteredUsers(context).find { it.email == email }
    }

    fun isEmailTaken(context: Context, email: String): Boolean {
        return getRegisteredUsers(context).any { it.email == email.lowercase().trim() }
    }

    private fun getRegisteredUsers(context: Context): List<User> {
        val json = getPrefs(context).getString(KEY_USERS, null) ?: return emptyList()
        return try { Json.decodeFromString(json) } catch (e: Exception) { emptyList() }
    }

    private fun saveUsers(context: Context, users: List<User>) {
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

    private fun getCurrentUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_CURRENT_USER_EMAIL, null)
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
