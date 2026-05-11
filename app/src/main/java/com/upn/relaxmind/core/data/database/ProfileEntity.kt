package com.upn.relaxmind.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String = "patient",
    val avatarUrl: String? = null,
    val condition: String? = null,
    val phone: String? = null,
    val tempLinkCode: String? = null,
    val tempLinkCodeExpiresAt: String? = null
)
