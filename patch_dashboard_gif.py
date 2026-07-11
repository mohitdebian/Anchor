import re

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "r") as f:
    content = f.read()

pattern = r'if \(uiState\.progress >= 1f\) \{\s*AsyncImage\([^}]+\}\.build\(\),\s*contentDescription = "Celebrate GIF"[^\)]+\)\s*\} else \{\s*AsyncImage\([^}]+\}\.build\(\),\s*contentDescription = "Cheer GIF"[^\)]+\)\s*\}'

content = re.sub(pattern, "", content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "w") as f:
    f.write(content)
