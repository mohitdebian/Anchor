import os

schedule_content = """package com.example.data.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val icon: String,
    val startTime: String,
    val endTime: String,
    val colorHex: String,
    val date: String = "",
    val timestamp: Long = 0L
)
"""

with open("app/src/main/java/com/example/data/models/Schedule.kt", "w") as f:
    f.write(schedule_content)
