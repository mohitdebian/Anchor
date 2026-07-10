import re

file_path = "app/src/main/java/com/example/viewmodels/DashboardViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'class DashboardViewModel(\n    private val focusRepository: FocusRepository,',
    'class DashboardViewModel(\n    private val application: android.app.Application,\n    private val focusRepository: FocusRepository,'
)

content = content.replace("sharedPreferences.edit().putBoolean(\"is_timer_active\", false).apply()\n            saveSession()", "sharedPreferences.edit().putBoolean(\"is_timer_active\", false).apply()\n        saveSession()")

with open(file_path, "w") as f:
    f.write(content)
