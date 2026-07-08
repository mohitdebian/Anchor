package com.example.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.Schedule
import com.example.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlannerViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {
    val schedules: StateFlow<List<Schedule>> = scheduleRepository.getAllSchedules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSchedule(title: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            scheduleRepository.insertSchedule(
                Schedule(
                    title = title,
                    icon = "🌙",
                    startTime = startTime,
                    endTime = endTime,
                    colorHex = "#D4AF37"
                )
            )
        }
    }
}
