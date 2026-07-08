package com.example.data.repository

import com.example.data.models.FocusSession
import com.example.data.database.FocusSessionDao
import kotlinx.coroutines.flow.Flow

class FocusRepository(private val dao: FocusSessionDao) {
    fun getAllSessions(): Flow<List<FocusSession>> = dao.getAllSessions()
    
    suspend fun insertSession(session: FocusSession) = dao.insertSession(session)
    
    fun getTotalFocusTimeSince(startTime: Long): Flow<Int?> = dao.getTotalFocusTimeSince(startTime)
}
