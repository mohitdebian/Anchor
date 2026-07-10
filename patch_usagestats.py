import re

file_path = "app/src/main/java/com/example/data/repository/UsageStatsRepository.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    fun getBlockedAppsCount(): Int {
        val sharedPreferences = context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
        return sharedPreferences.all.count { it.value == true }
    }

    fun getTopDistractingApps"""

content = content.replace("    fun getTopDistractingApps", replacement.strip())

with open(file_path, "w") as f:
    f.write(content)
