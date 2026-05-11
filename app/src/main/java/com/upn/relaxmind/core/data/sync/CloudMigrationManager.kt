package com.upn.relaxmind.core.data.sync

import android.content.Context
import com.upn.relaxmind.core.data.database.AppDatabase
import com.upn.relaxmind.core.data.database.CheckInEntity
import com.upn.relaxmind.core.data.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object CloudMigrationManager {
    suspend fun migrateIfNecessary(context: Context, userId: String) {
        val prefs = context.getSharedPreferences("relaxmind_migration", Context.MODE_PRIVATE)
        val isMigrated = prefs.getBoolean("is_migrated_to_cloud", false)

        if (!isMigrated) {
            withContext(Dispatchers.IO) {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val checkInDao = db.checkInDao()
                    val syncQueueDao = db.syncQueueDao()
                    val repository = CheckInRepository(checkInDao, syncQueueDao, context)

                    // 1. Leer viejos Check-Ins de AppPreferences (Ejemplo simulado)
                    val oldScore = AppPreferences.getWellnessScore(context)
                    if (oldScore > 0) {
                        // Creamos un CheckIn falso como base inicial migrada
                        val oldCheckIn = CheckInEntity(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            date = "2023-01-01",
                            wellnessScore = oldScore,
                            createdAt = "2023-01-01T00:00:00Z"
                        )
                        repository.saveCheckIn(oldCheckIn)
                    }

                    // 2. Marcar como migrado si todo salió bien
                    prefs.edit().putBoolean("is_migrated_to_cloud", true).apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
