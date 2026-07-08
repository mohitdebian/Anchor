package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.data.models.FocusSession

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Insert
    suspend fun insertSession(session: FocusSession)
    
    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE timestamp >= :startTime")
    fun getTotalFocusTimeSince(startTime: Long): Flow<Int?>
}
