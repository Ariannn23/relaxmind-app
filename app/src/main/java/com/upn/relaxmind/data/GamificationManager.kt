package com.upn.relaxmind.data

import android.content.Context
import com.upn.relaxmind.data.models.User
import java.text.SimpleDateFormat
import java.util.*

object GamificationManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun updateActivity(context: Context) {
        val user = AuthManager.getCurrentUser(context) ?: return
        if (user.role != "PATIENT") return

        val today = Calendar.getInstance()
        val todayStr = dateFormat.format(today.time)

        if (user.lastActivityDate == todayStr) return // Already updated today

        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(yesterday.time)

        var newStreak = user.streakCount
        if (user.lastActivityDate == yesterdayStr) {
            newStreak++
        } else if (user.lastActivityDate == null || isMoreThanOneDayAgo(user.lastActivityDate)) {
            newStreak = 1
        }

        val updatedBadges = user.earnedBadges.toMutableList()
        checkAndAwardBadges(newStreak, updatedBadges)

        // Save updated user
        val users = AuthManager.getRegisteredUsers(context).toMutableList()
        val index = users.indexOfFirst { it.email == user.email }
        if (index != -1) {
            users[index] = users[index].copy(
                streakCount = newStreak,
                lastActivityDate = todayStr,
                earnedBadges = updatedBadges
            )
            AuthManager.saveUsers(context, users)
        }
    }

    private fun isMoreThanOneDayAgo(lastDateStr: String?): Boolean {
        if (lastDateStr == null) return true
        return try {
            val lastDate = dateFormat.parse(lastDateStr) ?: return true
            val diff = Date().time - lastDate.time
            diff > (24 * 60 * 60 * 1000 * 1.5) // More than 1.5 days to be safe
        } catch (e: Exception) {
            true
        }
    }

    private fun checkAndAwardBadges(streak: Int, badges: MutableList<String>) {
        val badgeRules = mapOf(
            "streak_3" to 3,
            "streak_7" to 7,
            "streak_15" to 15,
            "streak_30" to 30
        )

        badgeRules.forEach { (id, required) ->
            if (streak >= required && !badges.contains(id)) {
                badges.add(id)
            }
        }
    }

    fun getBadgeDetails(badgeId: String): BadgeInfo {
        return when (badgeId) {
            "streak_3" -> BadgeInfo("Explorador", "3 días seguidos", "Iniciaste tu camino al bienestar.")
            "streak_7" -> BadgeInfo("Constante", "7 días seguidos", "Una semana completa de cuidado personal.")
            "streak_15" -> BadgeInfo("Dedicado", "15 días seguidos", "Tu mente está cada vez más fuerte.")
            "streak_30" -> BadgeInfo("Maestro Zen", "30 días seguidos", "Has alcanzado la maestría en bienestar.")
            else -> BadgeInfo("Medalla", "", "")
        }
    }
}

data class BadgeInfo(val name: String, val requirement: String, val description: String)
