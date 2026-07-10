import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace('text = "6 apps blocked",', 'text = "${uiState.blockedAppsCount} apps blocked",')

with open(file_path, "w") as f:
    f.write(content)
