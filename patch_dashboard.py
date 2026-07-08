import re

with open('app/src/main/java/com/example/viewmodels/DashboardViewModel.kt', 'r') as f:
    content = f.read()

pattern = r"(val minutes = totalMinutes \?\: 0\s+)(val goal = _uiState\.value\.dailyGoalMinutes\s+val progressValue = if \(goal > 0\) \(minutes\.toFloat\(\) \/ goal\)\.coerceIn\(0f, 1f\) else 0f\s+val screenTimeMinutes = usageStatsRepository\.getTodayScreenTimeMinutes\(\)\s+val screenTimeString = if \(screenTimeMinutes > 0\) formatTime\(screenTimeMinutes\) else if \(usageStatsRepository\.hasUsageStatsPermission\(\)\) \"0m\" else \"Needs Permission\"\s+_uiState\.value = _uiState\.value\.copy\([^)]+\))"

replacement = r"""\1userRepository.updateDailyProgress(minutes)
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
                )"""

new_content = re.sub(pattern, replacement, content)
with open('app/src/main/java/com/example/viewmodels/DashboardViewModel.kt', 'w') as f:
    f.write(new_content)
