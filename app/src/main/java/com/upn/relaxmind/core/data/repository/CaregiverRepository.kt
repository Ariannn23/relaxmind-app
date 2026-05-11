package com.upn.relaxmind.core.data.repository

import android.util.Log
import com.upn.relaxmind.core.data.network.SupabaseManager
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object CaregiverRepository {
    private val supabase = SupabaseManager.client

    suspend fun requestLinking(patientId: String, caregiverName: String) = withContext(Dispatchers.IO) {
        try {
            supabase.functions.invoke(
                "notify",
                body = buildJsonObject {
                    put("recipientUserId", patientId)
                    put("title", "Nueva solicitud de vinculación")
                    put("body", "$caregiverName quiere vincularse contigo como tu cuidador.")
                    putJsonObject("data") { put("type", "linking_request") }
                }
            )
        } catch (e: Exception) {
            Log.e("CaregiverRepo", "Error requesting linking", e)
        }
    }

    suspend fun acceptLinking(caregiverId: String, patientName: String) = withContext(Dispatchers.IO) {
        try {
            supabase.functions.invoke(
                "notify",
                body = buildJsonObject {
                    put("recipientUserId", caregiverId)
                    put("title", "¡Vinculación aceptada!")
                    put("body", "$patientName ha aceptado tu solicitud. Ahora puedes ver su progreso.")
                    putJsonObject("data") { put("type", "linking_success") }
                }
            )
        } catch (e: Exception) {
            Log.e("CaregiverRepo", "Error accepting linking", e)
        }
    }
}
