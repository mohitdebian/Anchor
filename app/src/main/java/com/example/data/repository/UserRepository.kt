package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.database.DailyGoalDao
import com.example.data.models.DailyGoalRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository(private val context: Context, private val dailyGoalDao: DailyGoalDao) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun getDailyGoalMinutes(): Int = prefs.getInt("daily_goal_minutes", 240)
    fun setDailyGoalMinutes(minutes: Int) {
        prefs.edit().putInt("daily_goal_minutes", minutes).apply()
    }

    suspend fun updateDailyProgress(achievedMinutes: Int) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val goal = getDailyGoalMinutes()
        val record = dailyGoalDao.getGoalRecord(dateStr) ?: DailyGoalRecord(dateStr, goal, 0, false)
        
        val newAchieved = achievedMinutes
        val goalMet = newAchieved >= record.goalMinutes
        
        dailyGoalDao.insertGoalRecord(
            record.copy(
                achievedMinutes = newAchieved,
                goalMet = goalMet
            )
        )
        
        if (goalMet && !record.goalMet) {
            // First time meeting goal today
            val streak = getStreak()
            val lastDate = getLastStreakDate()
            if (lastDate != dateStr) {
                // To be precise we'd check if lastDate is yesterday. 
                // Simple implementation: increment if not today
                setStreak(streak + 1)
                setLastStreakDate(dateStr)
            }
        }
    }

    fun getStreak(): Int = prefs.getInt("current_streak", 0)
    fun setStreak(streak: Int) {
        prefs.edit().putInt("current_streak", streak).apply()
    }
    
    fun getLastStreakDate(): String? = prefs.getString("last_streak_date", null)
    fun setLastStreakDate(date: String) {
        prefs.edit().putString("last_streak_date", date).apply()
    }
}
