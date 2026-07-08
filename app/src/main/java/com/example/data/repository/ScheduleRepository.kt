package com.example.data.repository

import com.example.data.database.ScheduleDao
import com.example.data.models.Schedule
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    fun getAllSchedules(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()

    suspend fun insertSchedule(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
    }
}
