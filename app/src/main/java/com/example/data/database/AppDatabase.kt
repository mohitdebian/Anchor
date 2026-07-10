package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.FocusSession
import com.example.data.models.Schedule

@Database(entities = [FocusSession::class, Schedule::class, com.example.data.models.DailyGoalRecord::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun dailyGoalDao(): DailyGoalDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "anchor_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
