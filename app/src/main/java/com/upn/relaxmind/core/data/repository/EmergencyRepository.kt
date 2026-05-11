package com.upn.relaxmind.core.data.repository

import android.util.Log
import com.upn.relaxmind.core.data.network.SupabaseManager
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object EmergencyRepository {
    private val supabase = SupabaseManager.client

    suspend fun triggerPanicAlert(patientName: String) = withContext(Dispatchers.IO) {
        try {
            val patientId = supabase.auth.currentUserOrNull()?.id ?: return@withContext

            val links = supabase.postgrest.from("patient_caregiver_links")
                .select {
                    filter {
                        eq("patient_id", patientId)
                        eq("status", "active")
                    }
                }
                .decodeList<Map<String, String>>()

            links.forEach { link ->
                val caregiverId = link["caregiver_id"] ?: return@forEach
                supabase.functions.invoke(
                    "notify",
                    body = buildJsonObject {
                        put("recipientUserId", caregiverId)
                        put("title", "🚨 ¡ALERTA DE EMERGENCIA!")
                        put("body", "$patientName necesita ayuda inmediata.")
                        putJsonObject("data") {
                            put("type", "crisis")
                            put("patientId", patientId)
                        }
                    }
                )
            }

            Log.d("EmergencyRepo", "Panic alerts sent to ${links.size} caregivers")
        } catch (e: Exception) {
            Log.e("EmergencyRepo", "Error triggering panic alert", e)
        }
    }
}
