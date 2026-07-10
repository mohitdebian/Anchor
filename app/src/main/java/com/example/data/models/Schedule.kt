package com.example.data.models
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
    val timestamp: Long = 0L,
    val blockedPackages: String = "" // Comma-separated list of package names
)
