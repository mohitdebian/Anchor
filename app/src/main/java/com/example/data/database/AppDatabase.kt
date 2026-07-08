package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.FocusSession
import com.example.data.models.Schedule

@Database(entities = [FocusSession::class, Schedule::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "anchor_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
