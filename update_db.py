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
    val date: String = "" // Added date in yyyy-MM-dd format
)
"""

with open("app/src/main/java/com/example/data/models/Schedule.kt", "w") as f:
    f.write(schedule_content)

db_file = "app/src/main/java/com/example/data/database/AppDatabase.kt"
with open(db_file, "r") as f:
    content = f.read()

content = content.replace('version = 3', 'version = 4')
content = content.replace('fallbackToDestructiveMigration(false)', 'fallbackToDestructiveMigration()')

with open(db_file, "w") as f:
    f.write(content)
