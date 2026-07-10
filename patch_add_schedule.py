import re

file_path = "app/src/main/java/com/example/ui/screens/planner/AddScheduleScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
fun AddScheduleScreen(
    dateStr: String = "",
    onNavigateBack: () -> Unit,
    viewModel: PlannerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var startTimeStr by remember { mutableStateOf("09:00 AM") }
    var endTimeStr by remember { mutableStateOf("10:00 AM") }
    
    val initialDate = if (dateStr.isNotEmpty()) {
        try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr) ?: Calendar.getInstance().time
        } catch (e: Exception) {
            Calendar.getInstance().time
        }
    } else {
        Calendar.getInstance().time
    }
    
    val targetDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(initialDate)
    val displayDateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(initialDate)
"""

content = re.sub(
    r'fun AddScheduleScreen\([\s\S]*?val displayDateStr = SimpleDateFormat\("MMM d, yyyy", Locale.getDefault\(\)\)\.format\(today\)',
    replacement.strip(),
    content
)

content = content.replace("viewModel.addSchedule(title, startTimeStr, endTimeStr, dateStr, selectedPackages)", "viewModel.addSchedule(title, startTimeStr, endTimeStr, targetDateStr, selectedPackages)")

with open(file_path, "w") as f:
    f.write(content)
