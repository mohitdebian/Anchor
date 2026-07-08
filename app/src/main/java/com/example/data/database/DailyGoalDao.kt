package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.models.DailyGoalRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Query("SELECT * FROM daily_goals ORDER BY date DESC")
    fun getAllGoalRecords(): Flow<List<DailyGoalRecord>>

    @Query("SELECT * FROM daily_goals WHERE date = :date LIMIT 1")
    suspend fun getGoalRecord(date: String): DailyGoalRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalRecord(record: DailyGoalRecord)
}
