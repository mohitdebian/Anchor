import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'onNavigateToAddSchedule: (String) -> Unit,',
    'onNavigateToAddSchedule: (String) -> Unit,\n    onNavigateToEditSchedule: (String, Int) -> Unit,'
)

replacement_icon = """
                                IconButton(onClick = { onNavigateToEditSchedule(schedule.date, schedule.id) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
                                }
                                IconButton(onClick = { viewModel.deleteSchedule(schedule) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                }
"""

content = re.sub(
    r'IconButton\(onClick = \{ viewModel\.deleteSchedule\(schedule\) \}\) \{\s*Icon\(Icons\.Default\.Delete, contentDescription = "Delete", tint = Color\.Red\.copy\(alpha = 0\.7f\)\)\s*\}',
    replacement_icon.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
