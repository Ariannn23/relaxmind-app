package com.upn.relaxmind.core.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upn.relaxmind.core.data.local.AppDatabase
import com.upn.relaxmind.core.data.network.SupabaseManager
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(applicationContext)
            val supabase = SupabaseManager.client

            // Sync unsynced diary entries
            val unsyncedDiary = db.diaryDao().getUnsyncedEntries()
            unsyncedDiary.forEach { entity ->
                supabase.postgrest.from("diary_entries").upsert(entity)
                db.diaryDao().updateEntry(entity.copy(isSynced = true))
            }

            // Sync unsynced reminders
            val unsyncedReminders = db.reminderDao().getUnsyncedReminders()
            unsyncedReminders.forEach { entity ->
                supabase.postgrest.from("reminders").upsert(entity)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
