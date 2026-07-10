import re

file_path = "app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Fix animation property
content = content.replace("var animationPlayed by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }", 
                          "val animationPlayed = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }")
content = content.replace("if (animationPlayed) 1f else 0f", "if (animationPlayed.value) 1f else 0f")
content = content.replace("animationPlayed = true", "animationPlayed.value = true")

# Fix syntax error at the end
# Remove trailing braces that might be extra
content = re.sub(r'\}\s*\}\s*$', '}', content)

with open(file_path, "w") as f:
    f.write(content)
