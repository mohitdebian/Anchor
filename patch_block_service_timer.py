import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                if (currentApp != null && currentApp != packageName) {
                    val isTimerActive = sharedPreferences.getBoolean("is_timer_active", false)
                    var isBlocked = false
                    
                    if (isTimerActive) {
                        isBlocked = sharedPreferences.getBoolean(currentApp, false)
                    }
                    
                    if (!isBlocked) {
"""

content = content.replace("""                if (currentApp != null && currentApp != packageName) {
                    var isBlocked = sharedPreferences.getBoolean(currentApp, false)
                    
                    if (!isBlocked) {""", replacement.strip())

with open(file_path, "w") as f:
    f.write(content)
