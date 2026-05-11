package com.upn.relaxmind.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val date: String,
    val wellnessScore: Int,
    val mood: String? = null,
    val sleepHours: Double? = null,
    val stressLevel: Int? = null,
    val createdAt: String
)
