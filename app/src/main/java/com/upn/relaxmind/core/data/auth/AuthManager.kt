package com.upn.relaxmind.core.data.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.upn.relaxmind.core.data.local.AppDatabase
import com.upn.relaxmind.core.data.local.entities.UserEntity
import com.upn.relaxmind.core.data.models.User
import com.upn.relaxmind.core.data.network.SupabaseManager
import com.upn.relaxmind.core.data.preferences.AppPreferences
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthManager {
    var isSessionUnlocked by mutableStateOf(false)
    private val supabase = SupabaseManager.client

    suspend fun registerUser(
        context: Context,
        name: String,
        email: String,
        password: String,
        role: String = "PATIENT",
        phoneNumber: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = supabase.auth.currentUserOrNull()?.id ?: return@withContext false

            // Insert profile into Supabase (only valid profile columns)
            supabase.postgrest.from("profiles").insert(
                mapOf(
                    "id" to userId,
                    "name" to name,
                    "email" to email,
                    "role" to role,
                    "phone_number" to phoneNumber
                )
            )
            // Cache locally
            val userEntity = UserEntity(id = userId, name = name, email = email, role = role, phoneNumber = phoneNumber)
            AppDatabase.getDatabase(context).userDao().insertUser(userEntity)
            true
        } catch (e: Exception) {
            Log.e("AuthManager", "Registration error", e)
            false
        }
    }

    suspend fun loginUser(context: Context, email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = supabase.auth.currentSessionOrNull() ?: return@withContext null
            val userId = session.user?.id ?: return@withContext null

            val profile = supabase.postgrest.from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeSingle<UserEntity>()

            AppDatabase.getDatabase(context).userDao().insertUser(profile)
            isSessionUnlocked = true

            User(
                id = profile.id,
                name = profile.name,
                email = profile.email,
                password = "",
                role = profile.role,
                phoneNumber = profile.phoneNumber
            )
        } catch (e: Exception) {
            Log.e("AuthManager", "Login error", e)
            null
        }
    }

    fun getCurrentUser(context: Context): User? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        return User(
            id = session.user?.id ?: "",
            name = AppPreferences.getDisplayName(context),
            email = session.user?.email ?: "",
            password = ""
        )
    }

    suspend fun updateProfile(context: Context, updates: Map<String, Any>) = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@withContext
            supabase.postgrest.from("profiles").update(updates) {
                filter { eq("id", userId) }
            }
            // Update local display name if provided
            (updates["name"] as? String)?.let { AppPreferences.saveDisplayName(context, it) }
        } catch (e: Exception) {
            Log.e("AuthManager", "Update profile error", e)
        }
    }

    suspend fun logout(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                supabase.auth.signOut()
                AppDatabase.getDatabase(context).userDao().clearAll()
            } catch (e: Exception) {
                Log.e("AuthManager", "Logout error", e)
            }
        }
        isSessionUnlocked = false
    }

    // Biometric support - delegates to AppPreferences
    fun isBiometricAvailable(context: Context): Boolean = AppPreferences.isBiometricEnabled(context)
    fun setBiometricEnabled(context: Context, enabled: Boolean) = AppPreferences.setBiometricEnabled(context, enabled)

    // Legacy linking/temp code stubs (for UI compatibility while Supabase migration is WIP)
    suspend fun generateTempCode(context: Context): String = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@withContext ""
            val code = (100000..999999).random().toString()
            val expiresAt = java.time.Instant.now().plusSeconds(300).toString()
            supabase.postgrest.from("temp_link_codes").upsert(
                mapOf("code" to code, "user_id" to userId, "expires_at" to expiresAt)
            )
            code
        } catch (e: Exception) {
            Log.e("AuthManager", "Generate temp code error", e)
            ""
        }
    }

    suspend fun validateTempCode(code: String): UserEntity? = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest.from("temp_link_codes")
                .select { filter { eq("code", code) } }
                .decodeSingleOrNull<UserEntity>()
        } catch (e: Exception) {
            Log.e("AuthManager", "Validate code error", e)
            null
        }
    }

    suspend fun getLinkedUsers(context: Context): List<User> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@withContext emptyList()
            val links = supabase.postgrest.from("patient_caregiver_links")
                .select { filter { eq("patient_id", userId); eq("status", "active") } }
                .decodeList<Map<String, String>>()

            links.mapNotNull { link ->
                val caregiverId = link["caregiver_id"] ?: return@mapNotNull null
                val profile = supabase.postgrest.from("profiles")
                    .select { filter { eq("id", caregiverId) } }
                    .decodeSingleOrNull<UserEntity>() ?: return@mapNotNull null
                User(id = profile.id, name = profile.name, email = profile.email, password = "", phoneNumber = profile.phoneNumber)
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Get linked users error", e)
            emptyList()
        }
    }

    suspend fun linkPatient(context: Context, patient: User, relationship: String = "") = withContext(Dispatchers.IO) {
        try {
            val caregiverId = supabase.auth.currentUserOrNull()?.id ?: return@withContext
            supabase.postgrest.from("patient_caregiver_links").upsert(
                mapOf(
                    "patient_id" to patient.id,
                    "caregiver_id" to caregiverId,
                    "status" to "pending",
                    "relationship" to relationship
                )
            )
        } catch (e: Exception) {
            Log.e("AuthManager", "Link patient error", e)
        }
    }

    suspend fun unlinkUser(context: Context, userId: String) = withContext(Dispatchers.IO) {
        try {
            val currentId = supabase.auth.currentUserOrNull()?.id ?: return@withContext
            supabase.postgrest.from("patient_caregiver_links").delete {
                filter {
                    or {
                        and { eq("patient_id", currentId); eq("caregiver_id", userId) }
                        and { eq("caregiver_id", currentId); eq("patient_id", userId) }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Unlink user error", e)
        }
    }

    suspend fun updateRelationship(context: Context, userId: String, relationship: String) = withContext(Dispatchers.IO) {
        try {
            val currentId = supabase.auth.currentUserOrNull()?.id ?: return@withContext
            supabase.postgrest.from("patient_caregiver_links").update(
                mapOf("relationship" to relationship)
            ) {
                filter { eq("caregiver_id", currentId); eq("patient_id", userId) }
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Update relationship error", e)
        }
    }

    suspend fun getRegisteredUsers(context: Context): List<User> = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest.from("profiles").select()
                .decodeList<UserEntity>()
                .map { User(id = it.id, name = it.name, email = it.email, password = "", role = it.role, phoneNumber = it.phoneNumber) }
        } catch (e: Exception) {
            Log.e("AuthManager", "Get registered users error", e)
            emptyList()
        }
    }

    suspend fun loginWithBiometrics(context: Context): User? {
        // Biometric just re-uses the cached Supabase session
        val session = supabase.auth.currentSessionOrNull() ?: return null
        isSessionUnlocked = true
        return User(
            id = session.user?.id ?: "",
            name = AppPreferences.getDisplayName(context),
            email = session.user?.email ?: "",
            password = ""
        )
    }
}
