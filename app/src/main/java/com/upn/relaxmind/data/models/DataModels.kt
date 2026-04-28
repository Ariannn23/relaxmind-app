package com.upn.relaxmind.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class ReminderType { MEDICAL_APPOINTMENT, MEDICATION }

@Serializable
data class Reminder(
    val id: String,
    val title: String,
    val type: ReminderType,
    val dateIso: String, // ISO date string (YYYY-MM-DD)
    val time: String,
    val dosage: String? = null,
    val doctor: String? = null,
    val location: String? = null,
    val frequency: String? = null,
    val notes: String? = null,
    val isCompleted: Boolean = false
)

@Serializable
data class DiaryEntry(
    val id: String,
    val dateIso: String,
    val text: String,
    val emotion: String,
    val tags: List<String> = emptyList()
)
