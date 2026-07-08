package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.FocusRepository
import com.example.data.repository.UsageStatsRepository
import com.example.data.repository.AppUsageInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

data class AnalyticsState(
    val totalTimeSaved: String = "0h 0m",
    val focusSessionsCount: String = "0",
    val weeklyData: List<Float> = listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f),
    val distractingApps: List<AppUsageInfo> = emptyList()
)

class AnalyticsViewModel(
    private val focusRepository: FocusRepository,
    private val usageStatsRepository: UsageStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsState())
    val uiState: StateFlow<AnalyticsState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val weekStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.timeInMillis

        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                usageStatsRepository.getTopDistractingApps(3)
            }
            
            focusRepository.getAllSessions().collectLatest { sessions ->
                val weekSessions = sessions.filter { it.timestamp >= weekStart }
                val totalMinutes = weekSessions.sumOf { it.durationMinutes }
                
                val h = totalMinutes / 60
                val m = totalMinutes % 60
                
                val chartData = MutableList(7) { 0.1f }
                
                if (weekSessions.isNotEmpty()) {
                    chartData[6] = (totalMinutes / 120f).coerceIn(0.1f, 1f)
                }

                _uiState.value = AnalyticsState(
                    totalTimeSaved = if (h > 0) "${h}h ${m}m" else "${m}m",
                    focusSessionsCount = sessions.size.toString(),
                    weeklyData = chartData,
                    distractingApps = apps
                )
            }
        }
    }
}
