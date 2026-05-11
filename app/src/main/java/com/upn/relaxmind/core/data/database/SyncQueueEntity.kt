package com.upn.relaxmind.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityType: String, // e.g., "check_ins", "diary_entries"
    val operationType: String, // e.g., "INSERT", "UPDATE", "DELETE"
    val entityId: String,
    val payloadJson: String? = null // For insert/update
)
