import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    var selectedDate by remember { mutableStateOf(dates.first()) }
    
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dbDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayFormatter = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val numFormatter = remember { SimpleDateFormat("d", Locale.getDefault()) }
    
    val selectedDateStr = dateFormatter.format(selectedDate)
    val dbDateStr = dbDateFormatter.format(selectedDate)
    val filteredSchedules = schedules.filter { it.date == dbDateStr }
"""

content = re.sub(
    r'var selectedDate by remember \{ mutableStateOf\(dates\.first\(\)\) \}[\s\S]*?val filteredSchedules = schedules\.filter \{ it\.date == selectedDateStr \}',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
