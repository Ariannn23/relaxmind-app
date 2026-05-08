package com.upn.relaxmind.data

import android.util.Log
import com.upn.relaxmind.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object LumiService {

    private const val TAG = "LumiDebug"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val API_URL = "https://api.groq.com/openai/v1/chat/completions"

    suspend fun getResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GROQ_API_KEY

            // LOG DE DIAGNÓSTICO — verifica que la key llega bien
            Log.d(TAG, "Key prefix: ${apiKey.take(8)}... Length: ${apiKey.length}")

            if (apiKey.isBlank()) {
                return@withContext "⚠️ GROQ_API_KEY está vacía. Verifica local.properties y haz Rebuild Project."
            }

            val systemInstruction = "Eres Lumi ✨, un asistente de bienestar emocional empático y cálido. " +
                    "Habla siempre en español. No des diagnósticos médicos. " +
                    "Tus respuestas deben ser breves, humanas y profesionales."

            // Construye el JSON con tipos correctos para la API de Groq
            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemInstruction)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val jsonBody = JSONObject().apply {
                put("model", "llama-3.1-8b-instant")
                put("messages", messagesArray)
                put("temperature", 0.7)        // Double — aceptado por Groq
                put("max_tokens", 1024)        // Int — correcto
                put("stream", false)           // Explícito: sin streaming
            }

            val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            Log.d(TAG, "Enviando petición a: $API_URL")
            Log.d(TAG, "Body: ${jsonBody.toString(2)}")

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Log.d(TAG, "Código HTTP: ${response.code}")
            Log.d(TAG, "Respuesta: $responseBody")

            if (!response.isSuccessful) {
                return@withContext "❌ Error ${response.code}: $responseBody"
            }

            if (responseBody.isBlank()) {
                return@withContext "El servidor respondió vacío. Intenta de nuevo."
            }

            val jsonResponse = JSONObject(responseBody)
            jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

        } catch (e: Exception) {
            Log.e(TAG, "Excepción al llamar a Groq", e)
            "⚠️ Error de conexión: ${e.javaClass.simpleName} — ${e.localizedMessage}"
        }
    }
}
