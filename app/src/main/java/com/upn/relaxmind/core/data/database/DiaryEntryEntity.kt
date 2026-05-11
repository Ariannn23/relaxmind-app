package com.upn.relaxmind.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val date: String,
    val content: String,
    val tags: List<String>,
    val createdAt: String
)
