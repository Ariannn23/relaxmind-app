package com.upn.relaxmind.data.models

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
    val biometricEnabled: Boolean = false
)
