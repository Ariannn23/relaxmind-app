package com.upn.relaxmind.core.data.migration

import android.content.Context
import android.util.Log
import com.upn.relaxmind.core.data.local.AppDatabase
import com.upn.relaxmind.core.data.local.entities.*
import com.upn.relaxmind.core.data.repository.LocalDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object MigrationManager {
    private const val PREFS_MIGRATION = "migration_prefs"
    private const val KEY_IS_MIGRATED = "is_migrated_to_room"

    suspend fun migrateIfNecessary(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_MIGRATION, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_IS_MIGRATED, false)) return

        Log.d("MigrationManager", "Starting migration from SharedPreferences to Room...")

        try {
            val db = AppDatabase.getDatabase(context)
            
            withContext(Dispatchers.IO) {
                // 1. Migrar Entradas de Diario
                val oldEntries = LocalDataRepository.getDiaryEntries(context)
                oldEntries.forEach { entry ->
                    db.diaryDao().insertEntry(
                        DiaryEntity(
                            id = entry.id,
                            userId = "LEGACY_USER", // Se actualizará al hacer login/sync
                            title = "Entrada antigua",
                            content = entry.text,
                            mood = entry.emotion,
                            createdAt = entry.dateIso,
                            isSynced = false
                        )
                    )
                }

                // 2. Migrar Recordatorios
                val oldReminders = LocalDataRepository.getReminders(context)
                oldReminders.forEach { reminder ->
                    db.reminderDao().insertReminder(
                        ReminderEntity(
                            id = reminder.id,
                            userId = "LEGACY_USER",
                            title = reminder.title,
                            description = reminder.notes ?: reminder.dosage,
                            time = reminder.time,
                            isEnabled = !reminder.isCompleted,
                            isSynced = false
                        )
                    )
                }
                
                // Marcar como completado
                prefs.edit().putBoolean(KEY_IS_MIGRATED, true).apply()
                Log.d("MigrationManager", "Migration completed successfully.")
            }
        } catch (e: Exception) {
            Log.e("MigrationManager", "Migration failed", e)
        }
    }
}
