package com.upn.relaxmind.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val lastName: String = "",
    val birthDate: String = "",
    val condition: String = "",
    val wellnessScore: Int = 0,
    val biometricEnabled: Boolean = false,
    val avatar: String? = null,
    val role: String = "PATIENT", // PATIENT or CAREGIVER
    val phoneNumber: String = "",
    val streakCount: Int = 0,
    val lastActivityDate: String? = null,
    val earnedBadges: List<String> = emptyList(),
    val linkedUserIds: List<String> = emptyList(), 
    val professionalRole: String? = null, // Global role: "Terapeuta", "Médico", etc.
    val patientRelationships: Map<String, String> = emptyMap() // patientId -> "Familiar", "Amigo", etc.
)
