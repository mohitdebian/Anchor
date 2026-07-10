package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.database.DailyGoalDao
import com.example.data.models.DailyGoalRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.text.ParseException
import java.util.Calendar
import java.util.Locale

class UserRepository(private val context: Context, private val dailyGoalDao: DailyGoalDao) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun getDailyGoalMinutes(): Int = prefs.getInt("daily_goal_minutes", 240)
    fun setDailyGoalMinutes(minutes: Int) {
        prefs.edit().putInt("daily_goal_minutes", minutes).apply()
    }

    fun validateAndGetStreak(): Int {
        val streak = getStreak()
        val lastDateStr = getLastStreakDate() ?: return streak
        
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val lastDate = format.parse(lastDateStr)
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            val lastDateCal = Calendar.getInstance()
            lastDateCal.time = lastDate
            lastDateCal.set(Calendar.HOUR_OF_DAY, 0)
            lastDateCal.set(Calendar.MINUTE, 0)
            lastDateCal.set(Calendar.SECOND, 0)
            lastDateCal.set(Calendar.MILLISECOND, 0)

            val diffMillis = today.timeInMillis - lastDateCal.timeInMillis
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)

            if (diffDays > 1) {
                // Streak broken
                setStreak(0)
                return 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getStreak()
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
        
        // Validate streak before updating
        val currentStreak = validateAndGetStreak()
        
        if (goalMet && !record.goalMet) {
            // First time meeting goal today
            val lastDate = getLastStreakDate()
            if (lastDate != dateStr) {
                // Determine if yesterday
                var isYesterday = false
                if (lastDate != null) {
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val lastDateParsed = format.parse(lastDate)
                        val today = Calendar.getInstance()
                        today.set(Calendar.HOUR_OF_DAY, 0)
                        today.set(Calendar.MINUTE, 0)
                        today.set(Calendar.SECOND, 0)
                        today.set(Calendar.MILLISECOND, 0)
                        
                        val lastCal = Calendar.getInstance()
                        lastCal.time = lastDateParsed
                        lastCal.set(Calendar.HOUR_OF_DAY, 0)
                        lastCal.set(Calendar.MINUTE, 0)
                        lastCal.set(Calendar.SECOND, 0)
                        lastCal.set(Calendar.MILLISECOND, 0)
                        
                        val diffMillis = today.timeInMillis - lastCal.timeInMillis
                        val diffDays = diffMillis / (1000 * 60 * 60 * 24)
                        if (diffDays == 1L) {
                            isYesterday = true
                        }
                    } catch (e: Exception) {}
                }
                
                if (isYesterday || lastDate == null || currentStreak == 0) {
                    setStreak(currentStreak + 1)
                } else {
                    // if it wasn't yesterday and streak > 0 but we somehow missed validate, we should have reset
                    setStreak(1)
                }
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
