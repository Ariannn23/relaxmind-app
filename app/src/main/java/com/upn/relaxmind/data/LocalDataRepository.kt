package com.upn.relaxmind.data

import android.content.Context
import android.content.SharedPreferences
import com.upn.relaxmind.data.models.DiaryEntry
import com.upn.relaxmind.data.models.Reminder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object LocalDataRepository {
    private const val PREFS_NAME = "relaxmind_local_data"
    private const val KEY_REMINDERS = "reminders_list"
    private const val KEY_DIARY = "diary_list"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val json = Json { ignoreUnknownKeys = true }

    // --- Reminders ---

    fun getReminders(context: Context): List<Reminder> {
        val jsonString = getPrefs(context).getString(KEY_REMINDERS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<Reminder>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveReminder(context: Context, reminder: Reminder) {
        val list = getReminders(context).toMutableList()
        val index = list.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            list[index] = reminder
        } else {
            list.add(reminder)
        }
        val jsonString = json.encodeToString(list)
        getPrefs(context).edit().putString(KEY_REMINDERS, jsonString).apply()
    }

    fun deleteReminder(context: Context, id: String) {
        val list = getReminders(context).filter { it.id != id }
        val jsonString = json.encodeToString(list)
        getPrefs(context).edit().putString(KEY_REMINDERS, jsonString).apply()
    }

    // --- Diary ---

    fun getDiaryEntries(context: Context): List<DiaryEntry> {
        val jsonString = getPrefs(context).getString(KEY_DIARY, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<DiaryEntry>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveDiaryEntry(context: Context, entry: DiaryEntry) {
        val list = getDiaryEntries(context).toMutableList()
        val index = list.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            list[index] = entry
        } else {
            list.add(entry)
        }
        val jsonString = json.encodeToString(list)
        getPrefs(context).edit().putString(KEY_DIARY, jsonString).apply()
    }
}
