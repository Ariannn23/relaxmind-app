package com.upn.relaxmind.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "profiles")
@Serializable
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    @SerialName("last_name") @ColumnInfo(name = "last_name") val lastName: String = "",
    val email: String,
    val role: String = "PATIENT",
    @SerialName("phone_number") @ColumnInfo(name = "phone_number") val phoneNumber: String = "",
    @SerialName("birth_date") @ColumnInfo(name = "birth_date") val birthDate: String = "",
    val condition: String = "",
    @SerialName("wellness_score") @ColumnInfo(name = "wellness_score") val wellnessScore: Int = 0,
    @SerialName("avatar_url") @ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,
    @SerialName("fcm_token") @ColumnInfo(name = "fcm_token") val fcmToken: String? = null,
    @kotlinx.serialization.Transient
    @ColumnInfo(name = "last_sync") val lastSync: Long = System.currentTimeMillis()
)

@Entity(tableName = "diary_entries")
@Serializable
data class DiaryEntity(
    @PrimaryKey val id: String,
    @SerialName("user_id") @ColumnInfo(name = "user_id") val userId: String,
    val title: String,
    val content: String,
    val mood: String,
    @SerialName("created_at") @ColumnInfo(name = "created_at") val createdAt: String,
    @kotlinx.serialization.Transient
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false
)

@Entity(tableName = "reminders")
@Serializable
data class ReminderEntity(
    @PrimaryKey val id: String,
    @SerialName("user_id") @ColumnInfo(name = "user_id") val userId: String,
    val title: String,
    val description: String?,
    val time: String,
    @SerialName("is_enabled") @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true,
    @kotlinx.serialization.Transient
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false
)
