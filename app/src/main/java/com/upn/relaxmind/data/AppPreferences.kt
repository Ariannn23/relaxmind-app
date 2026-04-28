package com.upn.relaxmind.data

import android.content.Context

object AppPreferences {
    private const val PREFS = "relaxmind_prefs"
    const val KEY_SELECTED_ROLE = "selected_role"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    const val KEY_BIOMETRIC_PROMPT_SHOWN = "biometric_prompt_shown"
    const val KEY_IS_GUEST = "is_guest"
    const val KEY_JUST_REGISTERED = "just_registered"
    const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
    const val KEY_DARK_MODE = "dark_mode"

    fun saveSelectedRole(context: Context, roleValue: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_ROLE, roleValue)
            .apply()
    }
    const val KEY_WELLNESS_SCORE = "wellness_score"
    const val KEY_DISPLAY_NAME = "display_name"
    const val KEY_BIRTH_DATE = "birth_date"
    const val KEY_CONDITION = "medical_condition"
    const val KEY_STREAK = "wellness_streak"

    fun saveWellnessScore(context: Context, score: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_WELLNESS_SCORE, score.coerceIn(0, 100))
            .apply()
    }

    fun getWellnessScore(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_WELLNESS_SCORE, 0)

    fun saveDisplayName(context: Context, name: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_DISPLAY_NAME, name.trim().ifBlank { "Usuario" })
            .apply()
    }

    fun getDisplayName(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_DISPLAY_NAME, null)?.ifBlank { null } ?: "Usuario"

    fun saveBirthDate(context: Context, date: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BIRTH_DATE, date)
            .apply()
    }

    fun getBirthDate(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_BIRTH_DATE, "15/05/1990") ?: "15/05/1990"

    fun saveCondition(context: Context, condition: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CONDITION, condition)
            .apply()
    }

    fun getCondition(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_CONDITION, "Paciente") ?: "Paciente"

    fun getStreak(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_STREAK, 1)

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    fun isBiometricEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun setBiometricPromptShown(context: Context, shown: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BIOMETRIC_PROMPT_SHOWN, shown)
            .apply()
    }

    fun isBiometricPromptShown(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_BIOMETRIC_PROMPT_SHOWN, false)

    fun setGuestMode(context: Context, isGuest: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_GUEST, isGuest)
            .apply()
    }

    fun isGuestMode(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_GUEST, false)

    fun setJustRegistered(context: Context, just: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_JUST_REGISTERED, just)
            .apply()
    }

    fun isJustRegistered(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_JUST_REGISTERED, false)

    fun setSeenOnboarding(context: Context, seen: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HAS_SEEN_ONBOARDING, seen)
            .apply()
    }

    fun hasSeenOnboarding(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAS_SEEN_ONBOARDING, false)

    fun setDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
    }

    fun isDarkMode(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val seenOnboarding = hasSeenOnboarding(context)
        val promptShown = isBiometricPromptShown(context)
        prefs.edit().clear().apply()
        if (seenOnboarding) setSeenOnboarding(context, true)
        if (promptShown) setBiometricPromptShown(context, true)
    }
}
