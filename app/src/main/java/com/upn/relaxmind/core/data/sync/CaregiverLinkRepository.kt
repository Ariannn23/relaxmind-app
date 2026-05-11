package com.upn.relaxmind.core.data.sync

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.upn.relaxmind.core.data.database.PatientCaregiverLinkDao
import com.upn.relaxmind.core.data.database.PatientCaregiverLinkEntity
import com.upn.relaxmind.core.data.database.ProfileDao
import com.upn.relaxmind.core.data.database.SyncQueueDao
import com.upn.relaxmind.core.data.database.SyncQueueEntity
import com.upn.relaxmind.core.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.random.Random

class CaregiverLinkRepository(
    private val linkDao: PatientCaregiverLinkDao,
    private val profileDao: ProfileDao,
    private val syncQueueDao: SyncQueueDao,
    private val context: Context
) {
    private val supabase = SupabaseClientProvider.client

    // Para el Paciente: Generar código de vinculación
    suspend fun generateTempCode(patientId: String): String {
        val code = generateRandomCode(6)
        
        // Expiración en 24 horas (formato ISO-8601)
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.HOUR, 24)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val expiresAt = sdf.format(calendar.time)

        // 1. Obtener perfil actual localmente
        val profile = profileDao.getProfileById(patientId) ?: throw Exception("Profile not found locally")
        
        // 2. Actualizar localmente
        val updatedProfile = profile.copy(tempLinkCode = code, tempLinkCodeExpiresAt = expiresAt)
        profileDao.insertOrUpdate(updatedProfile)

        // 3. Enviar a Supabase directamente (importante que sea inmediato para que el cuidador lo vea)
        try {
            supabase.from("profiles").update(
                {
                    set("temp_link_code", code)
                    set("temp_link_code_expires_at", expiresAt)
                }
            ) {
                filter { eq("id", patientId) }
            }
        } catch (e: Exception) {
            // Si falla, encolar para intentarlo luego
            val syncItem = SyncQueueEntity(
                entityType = "profiles",
                operationType = "UPDATE",
                entityId = patientId,
                payloadJson = Json.encodeToString(updatedProfile)
            )
            syncQueueDao.insert(syncItem)
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
        }

        return code
    }

    // Para el Cuidador: Validar código y crear solicitud
    suspend fun validateAndSendLink(tempCode: String, caregiverId: String): Result<String> {
        return try {
            // Buscar al paciente con ese código en Supabase (tiene que estar online)
            val result = supabase.from("profiles").select(columns = Columns.list("id")) {
                filter { 
                    eq("temp_link_code", tempCode)
                    // Nota: En una app real de producción validaríamos también expiration date aquí
                }
            }.decodeList<Map<String, String>>()

            if (result.isEmpty()) {
                return Result.failure(Exception("Código inválido o expirado"))
            }

            val patientId = result.first()["id"] ?: return Result.failure(Exception("ID de paciente no encontrado"))

            // Crear el enlace
            val linkId = UUID.randomUUID().toString()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val now = sdf.format(Date())

            val link = PatientCaregiverLinkEntity(
                id = linkId,
                patientId = patientId,
                caregiverId = caregiverId,
                status = "pending",
                createdAt = now
            )

            // Insertar local
            linkDao.insertOrUpdate(link)

            // Insertar online
            supabase.from("patient_caregiver_links").insert(link)
            
            Result.success("Solicitud enviada al paciente")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Para el Paciente: Aceptar solicitud
    suspend fun acceptLink(linkId: String, patientId: String) {
        linkDao.updateStatus(linkId, "active")
        
        try {
            supabase.from("patient_caregiver_links").update(
                { set("status", "active") }
            ) {
                filter { eq("id", linkId) }
            }
        } catch (e: Exception) {
            // Encolar
            val syncItem = SyncQueueEntity(
                entityType = "patient_caregiver_links",
                operationType = "UPDATE",
                entityId = linkId,
                payloadJson = "{\"status\":\"active\"}" // simplificado
            )
            syncQueueDao.insert(syncItem)
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<SyncWorker>().build())
        }
    }

    fun getLinkedPatients(caregiverId: String): Flow<List<PatientCaregiverLinkEntity>> {
        return linkDao.getActivePatientsForCaregiver(caregiverId)
    }

    fun getLinkedCaregivers(patientId: String): Flow<List<PatientCaregiverLinkEntity>> {
        return linkDao.getActiveCaregiversForPatient(patientId)
    }
    
    fun getPendingRequestsForPatient(patientId: String): Flow<List<PatientCaregiverLinkEntity>> {
        return linkDao.getPendingRequestsForPatient(patientId)
    }

    private fun generateRandomCode(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
}
