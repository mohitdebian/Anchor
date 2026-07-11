package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.FocusRepository
import com.example.data.repository.UsageStatsRepository
import com.example.data.repository.UserRepository
import com.example.data.models.FocusSession
import com.example.data.models.Schedule
import com.example.data.repository.ScheduleRepository
import java.text.SimpleDateFormat
import java.util.Locale
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
    val blockedAppsCount: Int = 0,
    val recentSessions: List<FocusSession> = emptyList(),
    val todaySchedules: List<Schedule> = emptyList(),
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
    val activeDurationMinutes: Int = 0,
    val isAmbientPlaying: Boolean = false
)

class DashboardViewModel(
    private val application: android.app.Application,
    private val focusRepository: FocusRepository,
    private val usageStatsRepository: UsageStatsRepository,
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    private val sharedPreferences = application.getSharedPreferences("blocked_apps", android.content.Context.MODE_PRIVATE)

    init {
        restoreTimerState()
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
                val streak = userRepository.validateAndGetStreak()
                
                val screenTimeMinutes = usageStatsRepository.getTodayScreenTimeMinutes()
                val blockedCount = usageStatsRepository.getBlockedAppsCount()
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
                    blockedAppsCount = blockedCount,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            scheduleRepository.getAllSchedules().collectLatest { schedules ->
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                _uiState.value = _uiState.value.copy(
                    todaySchedules = schedules.filter { it.date == todayStr }
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
        sharedPreferences.edit()
            .putBoolean("is_timer_active", true)
            .putBoolean("is_timer_paused", false)
            .putLong("timer_end_time", System.currentTimeMillis() + totalSeconds * 1000L)
            .putInt("total_timer_seconds", totalSeconds)
            .putInt("active_duration_minutes", durationMinutes)
            .apply()
            
        _uiState.value = _uiState.value.copy(
            isTimerActive = true,
            isTimerPaused = false,
            totalTimerSeconds = totalSeconds,
            remainingTimerSeconds = totalSeconds,
            activeDurationMinutes = durationMinutes
        )
        resumeTimerJob()
    }

    fun toggleAmbientSound(soundManager: com.example.services.AmbientSoundManager) {
        if (soundManager.isPlaying()) {
            soundManager.stop()
            _uiState.value = _uiState.value.copy(isAmbientPlaying = false)
        } else {
            soundManager.play()
            _uiState.value = _uiState.value.copy(isAmbientPlaying = true)
        }
    }

    fun stopTimer(soundManager: com.example.services.AmbientSoundManager) {
        soundManager.stop()
        _uiState.value = _uiState.value.copy(isAmbientPlaying = false)

        sharedPreferences.edit()
            .putBoolean("is_timer_active", false)
            .putBoolean("is_timer_paused", false)
            .apply()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerActive = false, isTimerPaused = false)
    }

    fun pauseTimer() {
        sharedPreferences.edit()
            .putBoolean("is_timer_active", false)
            .putBoolean("is_timer_paused", true)
            .putInt("remaining_seconds_when_paused", _uiState.value.remainingTimerSeconds)
            .apply()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerPaused = true)
    }

    fun resumeTimer() {
        sharedPreferences.edit()
            .putBoolean("is_timer_active", true)
            .putBoolean("is_timer_paused", false)
            .putLong("timer_end_time", System.currentTimeMillis() + _uiState.value.remainingTimerSeconds * 1000L)
            .apply()
        _uiState.value = _uiState.value.copy(isTimerPaused = false)
        resumeTimerJob()
    }

    private fun resumeTimerJob() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingTimerSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(
                    remainingTimerSeconds = _uiState.value.remainingTimerSeconds - 1
                )
            }
            sharedPreferences.edit().putBoolean("is_timer_active", false).apply()
            saveSession()
        }
    }
    
    private fun restoreTimerState() {
        val isActive = sharedPreferences.getBoolean("is_timer_active", false)
        val isPaused = sharedPreferences.getBoolean("is_timer_paused", false)
        val endTime = sharedPreferences.getLong("timer_end_time", 0L)
        val remainingWhenPaused = sharedPreferences.getInt("remaining_seconds_when_paused", 0)
        val totalSecs = sharedPreferences.getInt("total_timer_seconds", 0)
        val activeMins = sharedPreferences.getInt("active_duration_minutes", 0)

        if (isActive) {
            val now = System.currentTimeMillis()
            if (endTime > now) {
                val remaining = ((endTime - now) / 1000).toInt()
                _uiState.value = _uiState.value.copy(
                    isTimerActive = true,
                    isTimerPaused = false,
                    totalTimerSeconds = totalSecs,
                    remainingTimerSeconds = remaining,
                    activeDurationMinutes = activeMins
                )
                resumeTimerJob()
            } else {
                _uiState.value = _uiState.value.copy(activeDurationMinutes = activeMins)
                saveSession()
            }
        } else if (isPaused) {
            _uiState.value = _uiState.value.copy(
                isTimerActive = true,
                isTimerPaused = true,
                totalTimerSeconds = totalSecs,
                remainingTimerSeconds = remainingWhenPaused,
                activeDurationMinutes = activeMins
            )
        } else {
            sharedPreferences.edit().putBoolean("is_timer_active", false).apply()
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
