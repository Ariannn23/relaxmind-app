package com.upn.relaxmind.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncQueueDao {
    @Insert
    suspend fun insert(syncItem: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue ORDER BY id ASC")
    suspend fun getAllPending(): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: Long)
}
