import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'fun addSchedule(title: String, startTime: String, endTime: String, date: String, blockedPackages: String = "") {',
    'fun addSchedule(title: String, startTime: String, endTime: String, date: String, blockedPackages: String = "", id: Int = -1) {'
)

content = content.replace(
    'val schedule = Schedule(\n                title = title,',
    'val schedule = Schedule(\n                id = if (id != -1) id else 0,\n                title = title,'
)

with open(file_path, "w") as f:
    f.write(content)
