package com.upn.relaxmind

import android.app.Application
import androidx.work.*
import com.upn.relaxmind.core.data.migration.MigrationManager
import com.upn.relaxmind.core.data.sync.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RelaxMindApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        
        // Disparar migración asíncrona
        applicationScope.launch {
            MigrationManager.migrateIfNecessary(this@RelaxMindApplication)
        }

        // Configurar sincronización periódica
        setupSyncWorker()
    }

    private fun setupSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SupabaseSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
