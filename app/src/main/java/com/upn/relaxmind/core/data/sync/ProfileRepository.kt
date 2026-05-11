package com.upn.relaxmind.core.data.sync

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.upn.relaxmind.core.data.database.ProfileDao
import com.upn.relaxmind.core.data.database.ProfileEntity
import com.upn.relaxmind.core.data.database.SyncQueueDao
import com.upn.relaxmind.core.data.database.SyncQueueEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProfileRepository(
    private val profileDao: ProfileDao,
    private val syncQueueDao: SyncQueueDao,
    private val context: Context
) {
    suspend fun getProfile(id: String): ProfileEntity? {
        return profileDao.getProfileById(id)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        // Guardar localmente
        profileDao.insertOrUpdate(profile)
        
        // Encolar para sincronización
        val payload = Json.encodeToString(profile)
        val syncItem = SyncQueueEntity(
            entityType = "profiles",
            operationType = "UPDATE",
            entityId = profile.id,
            payloadJson = payload
        )
        syncQueueDao.insert(syncItem)
        
        // Disparar Worker
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
}
