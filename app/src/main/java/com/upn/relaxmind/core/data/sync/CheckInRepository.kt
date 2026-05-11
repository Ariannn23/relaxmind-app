package com.upn.relaxmind.core.data.sync

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.upn.relaxmind.core.data.database.CheckInDao
import com.upn.relaxmind.core.data.database.CheckInEntity
import com.upn.relaxmind.core.data.database.SyncQueueDao
import com.upn.relaxmind.core.data.database.SyncQueueEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class CheckInRepository(
    private val checkInDao: CheckInDao,
    private val syncQueueDao: SyncQueueDao,
    private val context: Context
) {
    fun getAllForUser(userId: String): Flow<List<CheckInEntity>> {
        return checkInDao.getAllForUser(userId)
    }

    suspend fun saveCheckIn(checkIn: CheckInEntity) {
        val checkInToSave = if (checkIn.id.isEmpty()) checkIn.copy(id = UUID.randomUUID().toString()) else checkIn
        
        // 1. Guardar localmente
        checkInDao.insert(checkInToSave)
        
        // 2. Encolar para sincronización
        val payload = Json.encodeToString(checkInToSave)
        val syncItem = SyncQueueEntity(
            entityType = "check_ins",
            operationType = "INSERT",
            entityId = checkInToSave.id,
            payloadJson = payload
        )
        syncQueueDao.insert(syncItem)
        
        // 3. Disparar Worker
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }

    suspend fun deleteCheckIn(id: String) {
        checkInDao.delete(id)
        val syncItem = SyncQueueEntity(
            entityType = "check_ins",
            operationType = "DELETE",
            entityId = id
        )
        syncQueueDao.insert(syncItem)
        
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
}
