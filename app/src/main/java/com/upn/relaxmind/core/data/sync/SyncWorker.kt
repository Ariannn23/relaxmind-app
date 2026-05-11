package com.upn.relaxmind.core.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upn.relaxmind.core.data.database.AppDatabase
import com.upn.relaxmind.core.data.database.CheckInEntity
import com.upn.relaxmind.core.data.supabase.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val syncQueueDao = database.syncQueueDao()
        val supabase = SupabaseClientProvider.client

        val pendingItems = syncQueueDao.getAllPending()

        if (pendingItems.isEmpty()) {
            return Result.success()
        }

        try {
            for (item in pendingItems) {
                val table = supabase.from(item.entityType)

                when (item.operationType) {
                    "INSERT", "UPDATE" -> {
                        item.payloadJson?.let { jsonString ->
                            when (item.entityType) {
                                "check_ins" -> {
                                    val checkIn = Json.decodeFromString<CheckInEntity>(jsonString)
                                    // Upsert into Supabase
                                    table.upsert(checkIn)
                                }
                                // Handle other entity types (diary, reminders) here
                            }
                        }
                    }
                    "DELETE" -> {
                        table.delete {
                            filter { eq("id", item.entityId) }
                        }
                    }
                }
                
                // If successful, remove from queue
                syncQueueDao.delete(item.id)
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // Retain in queue for next retry
            return Result.retry()
        }
    }
}
