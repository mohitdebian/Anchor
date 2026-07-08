package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoalRecord(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val goalMinutes: Int,
    val achievedMinutes: Int,
    val goalMet: Boolean
)
