import re

file_path = "app/src/main/java/com/example/ui/screens/planner/AddScheduleScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement_sig = """
fun AddScheduleScreen(
    dateStr: String = "",
    scheduleId: Int = -1,
    onNavigateBack: () -> Unit,
    viewModel: PlannerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    
    val existingSchedule = remember(scheduleId) {
        if (scheduleId != -1) viewModel.schedules.value.find { it.id == scheduleId } else null
    }

    var title by remember { mutableStateOf(existingSchedule?.title ?: "") }
    var startTimeStr by remember { mutableStateOf(existingSchedule?.startTime ?: "09:00 AM") }
    var endTimeStr by remember { mutableStateOf(existingSchedule?.endTime ?: "10:00 AM") }
    
    val initialDate = if (dateStr.isNotEmpty()) {
"""

content = re.sub(
    r'fun AddScheduleScreen\([\s\S]*?var endTimeStr by remember \{ mutableStateOf\("10:00 AM"\) \}\n\s*val initialDate = if \(dateStr\.isNotEmpty\(\)\) \{',
    replacement_sig.strip() + "\n",
    content
)

# Update the save logic to include id
save_logic = """
                        if (title.isNotBlank()) {
                            val selectedPackages = apps.filter { it.isSelected }.joinToString(",") { it.packageName }
                            viewModel.addSchedule(title, startTimeStr, endTimeStr, targetDateStr, selectedPackages, scheduleId)
                            onNavigateBack()
                        }
"""

content = re.sub(
    r'if \(title\.isNotBlank\(\)\) \{\s*val selectedPackages = apps\.filter \{ it\.isSelected \}\.joinToString\(","\) \{ it\.packageName \}\s*viewModel\.addSchedule\(title, startTimeStr, endTimeStr, targetDateStr, selectedPackages\)\s*onNavigateBack\(\)\s*\}',
    save_logic.strip(),
    content
)

# Also pre-select apps if existingSchedule is not null
app_logic = """
            val uniqueApps = appList.distinctBy { it.packageName }.sortedBy { it.name }
            
            if (existingSchedule != null) {
                val blockedList = existingSchedule.blockedPackages.split(",")
                uniqueApps.forEach { app ->
                    if (blockedList.contains(app.packageName)) {
                        app.isSelected = true
                    }
                }
            }
            
            apps = uniqueApps
"""

content = content.replace("val uniqueApps = appList.distinctBy { it.packageName }.sortedBy { it.name }\n            apps = uniqueApps", app_logic.strip())

with open(file_path, "w") as f:
    f.write(content)
