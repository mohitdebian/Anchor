import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'onNavigateToAddSchedule = { date -> navController.navigate("add_schedule/$date") },',
    'onNavigateToAddSchedule = { date -> navController.navigate("add_schedule/$date") },\n                            onNavigateToEditSchedule = { date, id -> navController.navigate("add_schedule/$date?scheduleId=$id") },'
)

with open(file_path, "w") as f:
    f.write(content)
