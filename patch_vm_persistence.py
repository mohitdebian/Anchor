with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "r") as f:
    content = f.read()

import re

# Replace init
init_pattern = re.compile(r'init \{\n\s*sharedPreferences\.edit\(\)\.putBoolean\("is_timer_active", false\)\.apply\(\)\n\s*loadDashboardData\(\)\n\s*\}')
init_replacement = """init {
        restoreTimerState()
        loadDashboardData()
    }"""
content = init_pattern.sub(init_replacement, content)

# Replace startTimer
start_timer_pattern = re.compile(r'fun startTimer\(durationMinutes: Int\) \{[\s\S]*?timerJob = viewModelScope\.launch \{[\s\S]*?sharedPreferences\.edit\(\)\.putBoolean\("is_timer_active", false\)\.apply\(\)\n\s*saveSession\(\)\n\s*\}\n\s*\}')
start_timer_replacement = """fun startTimer(durationMinutes: Int) {
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
    }"""
content = start_timer_pattern.sub(start_timer_replacement, content)

# Replace stopTimer
stop_timer_pattern = re.compile(r'fun stopTimer\(\) \{[\s\S]*?\}')
stop_timer_replacement = """fun stopTimer() {
        sharedPreferences.edit()
            .putBoolean("is_timer_active", false)
            .putBoolean("is_timer_paused", false)
            .apply()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerActive = false, isTimerPaused = false)
    }"""
content = stop_timer_pattern.sub(stop_timer_replacement, content)

# Replace pauseTimer
pause_timer_pattern = re.compile(r'fun pauseTimer\(\) \{[\s\S]*?\}')
pause_timer_replacement = """fun pauseTimer() {
        sharedPreferences.edit()
            .putBoolean("is_timer_active", false)
            .putBoolean("is_timer_paused", true)
            .putInt("remaining_seconds_when_paused", _uiState.value.remainingTimerSeconds)
            .apply()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerPaused = true)
    }"""
content = pause_timer_pattern.sub(pause_timer_replacement, content)

# Replace resumeTimer
resume_timer_pattern = re.compile(r'fun resumeTimer\(\) \{[\s\S]*?timerJob = viewModelScope\.launch \{[\s\S]*?sharedPreferences\.edit\(\)\.putBoolean\("is_timer_active", false\)\.apply\(\)\n\s*saveSession\(\)\n\s*\}\n\s*\}')
resume_timer_replacement = """fun resumeTimer() {
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
    }"""
content = resume_timer_pattern.sub(resume_timer_replacement, content)

with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "w") as f:
    f.write(content)
