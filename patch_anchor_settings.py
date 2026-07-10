import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } }',
    'onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } },\n                            onNavigateToSettings = { navController.navigate("settings") }'
)

with open(file_path, "w") as f:
    f.write(content)
