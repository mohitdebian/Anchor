import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'onNavigateToBlocks: () -> Unit,',
    'onNavigateToBlocks: () -> Unit,\n    onNavigateToAddSchedule: () -> Unit,'
)

with open(file_path, "w") as f:
    f.write(content)
