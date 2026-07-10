with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("fun startTimer(durationMinutes: Int) {\n        val totalSeconds = durationMinutes * 60\n        _uiState.value = _uiState.value.copy(", """fun startTimer(durationMinutes: Int) {
        sharedPreferences.edit().putBoolean("is_timer_active", true).apply()
        val totalSeconds = durationMinutes * 60
        _uiState.value = _uiState.value.copy(""")

with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "w") as f:
    f.write(content)
