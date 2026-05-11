package com.upn.relaxmind.core.data.repository

import android.os.Build
import android.util.Log
import com.upn.relaxmind.core.data.network.SupabaseManager
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DeviceRepository {
    private val supabase = SupabaseManager.client

    suspend fun registerDeviceToken(token: String) = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@withContext

            supabase.postgrest.from("user_devices").upsert(
                mapOf(
                    "user_id" to userId,
                    "fcm_token" to token,
                    "device_name" to "${Build.MANUFACTURER} ${Build.MODEL}"
                ),
                onConflict = "user_id,fcm_token"
            )

            Log.d("DeviceRepository", "FCM Token registered for user $userId")
        } catch (e: Exception) {
            Log.e("DeviceRepository", "Error registering device token", e)
        }
    }
}
