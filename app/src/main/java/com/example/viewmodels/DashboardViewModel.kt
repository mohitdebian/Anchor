package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.FocusRepository
import com.example.data.repository.UsageStatsRepository
import com.example.data.repository.UserRepository
import com.example.data.models.FocusSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardState(
    val progress: Float = 0f,
    val focusTimeToday: String = "0m",
    val focusMinutesToday: Int = 0,
    val screenTime: String = "...",
    val focusStreak: String = "...",
    val insightText: String = "Loading...",
    val blockedAlerts: Int = 0,
    val recentSessions: List<FocusSession> = emptyList(),
    val isLoading: Boolean = true,
    val dailyGoalMinutes: Int = 240,
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhotoUrl: String? = null,
    
    // Timer properties
    val isTimerActive: Boolean = false,
    val isTimerPaused: Boolean = false,
    val totalTimerSeconds: Int = 0,
    val remainingTimerSeconds: Int = 0,
    val activeDurationMinutes: Int = 0
)

class DashboardViewModel(
    private val focusRepository: FocusRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            dailyGoalMinutes = userRepository.getDailyGoalMinutes(),
            focusStreak = "${userRepository.getStreak()} Days"
        )
        loadDashboardData()
    }

    fun loadDashboardData() {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        viewModelScope.launch {
            focusRepository.getTotalFocusTimeSince(todayStart).collectLatest { totalMinutes ->
                val minutes = totalMinutes ?: 0
                
                userRepository.updateDailyProgress(minutes)
                
                val goal = userRepository.getDailyGoalMinutes()
                val progressValue = if (goal > 0) (minutes.toFloat() / goal).coerceIn(0f, 1f) else 0f
                val streak = userRepository.getStreak()
                
                val screenTimeMinutes = usageStatsRepository.getTodayScreenTimeMinutes()
                val screenTimeString = if (screenTimeMinutes > 0) formatTime(screenTimeMinutes) else if (usageStatsRepository.hasUsageStatsPermission()) "0m" else "Needs Permission"

                _uiState.value = _uiState.value.copy(
                    dailyGoalMinutes = goal,
                    progress = progressValue,
                    focusTimeToday = formatTime(minutes),
                    focusMinutesToday = minutes,
                    screenTime = screenTimeString,
                    focusStreak = "$streak Days",
                    insightText = if (minutes >= goal) "You've hit your daily goal! Excellent focus today." else if (minutes > 0) "Great job focusing today! Keep up the momentum." else "You haven't focused yet today. Start a session now!",
                    blockedAlerts = 0,
                    isLoading = false
                )
            }
        }
        
        viewModelScope.launch {
            focusRepository.getAllSessions().collectLatest { sessions ->
                _uiState.value = _uiState.value.copy(
                    recentSessions = sessions.take(5)
                )
            }
        }
    }
    
    fun requestUsagePermission(context: android.content.Context) {
        usageStatsRepository.requestUsageStatsPermission(context)
    }

    fun updateProfile(name: String?, email: String?, photoUrl: String?) {
        _uiState.value = _uiState.value.copy(
            userName = name,
            userEmail = email,
            userPhotoUrl = photoUrl
        )
    }

    fun updateDailyGoal(minutes: Int) {
        userRepository.setDailyGoalMinutes(minutes)
        val currentMinutes = _uiState.value.focusMinutesToday
        val progressValue = if (minutes > 0) (currentMinutes.toFloat() / minutes).coerceIn(0f, 1f) else 0f
        _uiState.value = _uiState.value.copy(
            dailyGoalMinutes = minutes,
            progress = progressValue
        )
    }

    private var timerJob: kotlinx.coroutines.Job? = null

    fun startTimer(durationMinutes: Int) {
        val totalSeconds = durationMinutes * 60
        _uiState.value = _uiState.value.copy(
            isTimerActive = true,
            isTimerPaused = false,
            totalTimerSeconds = totalSeconds,
            remainingTimerSeconds = totalSeconds,
            activeDurationMinutes = durationMinutes
        )
        
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTimerSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(
                    remainingTimerSeconds = _uiState.value.remainingTimerSeconds - 1
                )
            }
            saveSession()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerActive = false, isTimerPaused = false)
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerPaused = true)
    }

    fun resumeTimer() {
        _uiState.value = _uiState.value.copy(isTimerPaused = false)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTimerSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(
                    remainingTimerSeconds = _uiState.value.remainingTimerSeconds - 1
                )
            }
            saveSession()
        }
    }

    private fun saveSession() {
        timerJob?.cancel()
        viewModelScope.launch {
            focusRepository.insertSession(
                com.example.data.models.FocusSession(
                    durationMinutes = _uiState.value.activeDurationMinutes
                )
            )
            _uiState.value = _uiState.value.copy(isTimerActive = false, isTimerPaused = false)
            loadDashboardData()
        }
    }

    private fun formatTime(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}
