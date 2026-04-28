package com.upn.relaxmind.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * LumiService gestiona la comunicación con la API de Gemini.
 * Proporciona una interfaz simple para obtener respuestas empáticas de la IA.
 */
object LumiService {
    // IMPORTANTE: Reemplaza con tu API Key real de Google AI Studio (https://aistudio.google.com/)
    private const val API_KEY = "REEMPLAZA_CON_TU_API_KEY"
    
    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = API_KEY,
            systemInstruction = content { 
                text("Eres Lumi ✨, un asistente de bienestar emocional empático, cálido y profesional. " +
                     "Tu objetivo es escuchar al usuario, validar sus emociones y ofrecer apoyo. " +
                     "Siempre hablas en español. " +
                     "Si el usuario está ansioso, sugiere ejercicios de respiración. " +
                     "Si está triste, ofrece palabras de aliento y validación. " +
                     "Nunca des diagnósticos médicos ni sustituyas a un profesional de la salud mental. " +
                     "Mantén tus respuestas concisas pero muy cálidas.") 
            }
        )
    }

    /**
     * Envía un mensaje a la IA y retorna la respuesta procesada.
     */
    suspend fun getResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (API_KEY == "REEMPLAZA_CON_TU_API_KEY") {
                return@withContext "Hola. Para que pueda hablar contigo de forma inteligente, necesitas configurar mi API Key en LumiService.kt."
            }
            
            val response = model.generateContent(prompt)
            response.text ?: "Lo siento, me quedé pensando un momento... ¿puedes decirme más sobre eso?"
        } catch (e: Exception) {
            e.printStackTrace()
            "Parece que tengo problemas para conectarme ahora mismo, pero sigo aquí para escucharte. Cuéntame más."
        }
    }
}
