import re

file_path = "app/src/main/java/com/example/viewmodels/AnalyticsViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    private fun loadData() {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                usageStatsRepository.getTopDistractingApps(5)
            }
            
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
                    distractingApps = apps
                )
            }
        }
    }
"""

content = re.sub(
    r'private fun loadData\(\) \{.*?\}\s*\}',
    replacement.strip(),
    content,
    flags=re.DOTALL
)

with open(file_path, "w") as f:
    f.write(content)
