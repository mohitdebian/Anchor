import re

# Fix PlannerScreen.kt
file_path_screen = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path_screen, "r") as f:
    content_screen = f.read()

content_screen = content_screen.replace("androidx.compose.material.icons.filled.Edit", "Icons.Default.Edit")
content_screen = content_screen.replace("androidx.compose.material.icons.filled.Delete", "Icons.Default.Delete")

if "import androidx.compose.material.icons.filled.Edit" not in content_screen:
    content_screen = content_screen.replace("import androidx.compose.material.icons.filled.Add", "import androidx.compose.material.icons.filled.Add\nimport androidx.compose.material.icons.filled.Edit\nimport androidx.compose.material.icons.filled.Delete")

with open(file_path_screen, "w") as f:
    f.write(content_screen)

# Fix PlannerViewModel.kt
file_path_vm = "app/src/main/java/com/example/ui/screens/planner/PlannerViewModel.kt"
with open(file_path_vm, "r") as f:
    content_vm = f.read()

# Fix the method nesting
content_vm = re.sub(
    r'\}\s*catch\s*\(e:\s*Exception\)\s*\{\s*e\.printStackTrace\(\)\s*return\s*0L\s*\}\s*fun\s*deleteSchedule\(schedule:\s*Schedule\)\s*\{',
    r'} catch (e: Exception) {\n            e.printStackTrace()\n            return 0L\n        }\n    }\n\n    fun deleteSchedule(schedule: Schedule) {',
    content_vm
)

# Fix AlarmScheduler call
content_vm = content_vm.replace("AlarmScheduler.cancelAlarm(getApplication(), schedule.timestamp.hashCode())", "com.example.AlarmScheduler.cancelAlarm(getApplication(), schedule.timestamp.hashCode())")

with open(file_path_vm, "w") as f:
    f.write(content_vm)
