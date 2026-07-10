import os

# AppViewModelProvider.kt
with open('app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt', 'r') as f:
    content = f.read()

content = content.replace(
"""        initializer {
            com.example.ui.screens.planner.PlannerViewModel(
                anchorApplication().container.scheduleRepository
            )
        }""",
"""        initializer {
            com.example.ui.screens.planner.PlannerViewModel(
                anchorApplication(),
                anchorApplication().container.scheduleRepository
            )
        }"""
)

with open('app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt', 'w') as f:
    f.write(content)

# PlannerViewModel.kt
new_planner_vm = """package com.example.ui.screens.planner

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
        
    fun addSchedule(title: String, startTime: String, endTime: String, date: String) {
        viewModelScope.launch {
            val timestamp = parseTimestamp(date, startTime)
            val schedule = Schedule(
                title = title,
                icon = "🗓️",
                startTime = startTime,
                endTime = endTime,
                colorHex = "#10B981",
                date = date,
                timestamp = timestamp
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
            val format = SimpleDateFormat("MMM d, yyyy hh:mm a", Locale.getDefault())
            val date = format.parse("$dateStr $timeStr")
            return date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }
}
"""
with open('app/src/main/java/com/example/ui/screens/planner/PlannerViewModel.kt', 'w') as f:
    f.write(new_planner_vm)

