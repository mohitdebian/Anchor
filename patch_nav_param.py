import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },',
    'onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },\n                            onNavigateToAddSchedule = { navController.navigate("add_schedule") },'
)

with open(file_path, "w") as f:
    f.write(content)
