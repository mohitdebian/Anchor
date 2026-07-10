package com.example.ui.screens.planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AlarmScheduler
import com.example.data.models.Schedule
import com.example.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlannerViewModel(
    application: Application,
    private val scheduleRepository: ScheduleRepository
) : AndroidViewModel(application) {
    val schedules: StateFlow<List<Schedule>> = scheduleRepository.getAllSchedules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    fun addSchedule(title: String, startTime: String, endTime: String, date: String, blockedPackages: String = "", id: Int = -1) {
        viewModelScope.launch {
            val timestamp = parseTimestamp(date, startTime)
            val schedule = Schedule(
                id = if (id != -1) id else 0,
                title = title,
                icon = "🗓️",
                startTime = startTime,
                endTime = endTime,
                colorHex = "#10B981",
                date = date,
                timestamp = timestamp,
                blockedPackages = blockedPackages
            )
            scheduleRepository.insertSchedule(schedule)
            
            val requestCode = timestamp.hashCode()
            
            if (timestamp > System.currentTimeMillis()) {
                AlarmScheduler.scheduleAlarm(
                    context = getApplication(),
                    timestamp = timestamp,
                    title = "Scheduled Session: $title",
                    message = "It's time to start your scheduled focus session!",
                    requestCode = requestCode
                )
            }
        }
    }
    
    private fun parseTimestamp(dateStr: String, timeStr: String): Long {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault())
            val date = format.parse("$dateStr $timeStr")
            return date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            com.example.AlarmScheduler.cancelAlarm(getApplication(), schedule.timestamp.hashCode())
        }
    }
}

