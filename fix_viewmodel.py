import re

with open('app/src/main/java/com/example/viewmodels/DashboardViewModel.kt', 'r') as f:
    content = f.read()

pattern = r"_uiState\.value = _uiState\.value\.copy\([\s\S]*?isLoading = false\s*\)\,\s*focusMinutesToday = minutes,[\s\S]*?isLoading = false\s*\)"
replacement = r"""_uiState.value = _uiState.value.copy(
                    dailyGoalMinutes = goal,
                    progress = progressValue,
                    focusTimeToday = formatTime(minutes),
                    focusMinutesToday = minutes,
                    screenTime = screenTimeString,
                    focusStreak = "$streak Days",
                    insightText = if (minutes >= goal) "You've hit your daily goal! Excellent focus today." else if (minutes > 0) "Great job focusing today! Keep up the momentum." else "You haven't focused yet today. Start a session now!",
                    blockedAlerts = 0,
                    isLoading = false
                )"""

new_content = re.sub(pattern, replacement, content)
with open('app/src/main/java/com/example/viewmodels/DashboardViewModel.kt', 'w') as f:
    f.write(new_content)
