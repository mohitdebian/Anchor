import re

file_path = "app/src/main/java/com/example/viewmodels/DashboardViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("val blockedAlerts: Int = 0,", "val blockedAlerts: Int = 0,\n    val blockedAppsCount: Int = 0,")

# Update in loadDashboardData
content = content.replace("val screenTimeMinutes = usageStatsRepository.getTodayScreenTimeMinutes()", "val screenTimeMinutes = usageStatsRepository.getTodayScreenTimeMinutes()\n                val blockedCount = usageStatsRepository.getBlockedAppsCount()")
content = content.replace("blockedAlerts = 0,", "blockedAlerts = 0,\n                    blockedAppsCount = blockedCount,")

with open(file_path, "w") as f:
    f.write(content)
