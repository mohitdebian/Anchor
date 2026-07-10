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
    val distractingApps: List<AppUsageInfo> = emptyList(),
    val screenTimeMinutes: Int = 0,
    val hasUsagePermission: Boolean = false
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
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                usageStatsRepository.getTopDistractingApps(5)
            }
            val screenTime = withContext(Dispatchers.IO) {
                usageStatsRepository.getTodayScreenTimeMinutes()
            }
            val hasPerm = usageStatsRepository.hasUsageStatsPermission()
            
            focusRepository.getAllSessions().collectLatest { sessions ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                
                val todayStart = calendar.timeInMillis
                val weekStart = todayStart - (6L * 24 * 60 * 60 * 1000) // 7 days ago
                
                val allTimeMinutes = sessions.sumOf { it.durationMinutes }
                val h = allTimeMinutes / 60
                val m = allTimeMinutes % 60
                
                // Calculate daily totals
                val dailyTotals = FloatArray(7) { 0f }
                for (session in sessions) {
                    if (session.timestamp >= weekStart) {
                        val dayIndex = ((session.timestamp - weekStart) / (24L * 60 * 60 * 1000)).toInt().coerceIn(0, 6)
                        dailyTotals[dayIndex] += session.durationMinutes.toFloat()
                    }
                }
                
                val maxDaily = dailyTotals.maxOrNull()?.coerceAtLeast(60f) ?: 60f
                val chartData = dailyTotals.map { (it / maxDaily).coerceIn(0.05f, 1f) }
                
                _uiState.value = AnalyticsState(
                    totalTimeSaved = if (h > 0) "${h}h ${m}m" else "${m}m",
                    focusSessionsCount = sessions.size.toString(),
                    weeklyData = chartData,
                    distractingApps = apps,
                    screenTimeMinutes = screenTime,
                    hasUsagePermission = hasPerm
                )
            }
        }
    }
    }