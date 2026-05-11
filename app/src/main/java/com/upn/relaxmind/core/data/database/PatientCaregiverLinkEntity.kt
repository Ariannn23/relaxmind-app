package com.upn.relaxmind.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "patient_caregiver_links")
data class PatientCaregiverLinkEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val caregiverId: String,
    val status: String = "pending", // pending, active, rejected
    val tempCode: String? = null,
    val tempCodeExpiresAt: String? = null,
    val createdAt: String
)
