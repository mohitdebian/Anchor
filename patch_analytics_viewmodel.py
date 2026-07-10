import re

file_path = "app/src/main/java/com/example/viewmodels/AnalyticsViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = re.sub(
    r'val distractingApps: List<AppUsageInfo> = emptyList\(\)', 
    'val distractingApps: List<AppUsageInfo> = emptyList(),\n    val screenTimeMinutes: Int = 0,\n    val hasUsagePermission: Boolean = false', 
    content
)

content = re.sub(
    r'val apps = withContext\(Dispatchers\.IO\) \{\n\s*usageStatsRepository\.getTopDistractingApps\(5\)\n\s*\}', 
    'val apps = withContext(Dispatchers.IO) {\n                usageStatsRepository.getTopDistractingApps(5)\n            }\n            val screenTime = withContext(Dispatchers.IO) {\n                usageStatsRepository.getTodayScreenTimeMinutes()\n            }\n            val hasPerm = usageStatsRepository.hasUsageStatsPermission()', 
    content
)

content = re.sub(
    r'distractingApps = apps\n\s*\)', 
    'distractingApps = apps,\n                    screenTimeMinutes = screenTime,\n                    hasUsagePermission = hasPerm\n                )', 
    content
)

with open(file_path, "w") as f:
    f.write(content)
