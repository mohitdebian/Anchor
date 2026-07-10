import re

file_path = "app/src/main/java/com/example/viewmodels/DashboardViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement_init = """
    private val sharedPreferences = application.getSharedPreferences("blocked_apps", android.content.Context.MODE_PRIVATE)

    init {
        sharedPreferences.edit().putBoolean("is_timer_active", false).apply()
        loadDashboardData()
    }
"""
content = re.sub(r'init \{[\s\S]*?loadDashboardData\(\)\s*\}', replacement_init.strip(), content)

content = content.replace("fun resumeTimer() {", "fun resumeTimer() {\n        sharedPreferences.edit().putBoolean(\"is_timer_active\", true).apply()")
content = content.replace("fun pauseTimer() {", "fun pauseTimer() {\n        sharedPreferences.edit().putBoolean(\"is_timer_active\", false).apply()")
content = content.replace("fun stopTimer() {", "fun stopTimer() {\n        sharedPreferences.edit().putBoolean(\"is_timer_active\", false).apply()")

# also stopTimer when timer finishes
content = content.replace("saveSession()", "sharedPreferences.edit().putBoolean(\"is_timer_active\", false).apply()\n            saveSession()")

with open(file_path, "w") as f:
    f.write(content)
