import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }
"""

content = content.replace("    val context = LocalContext.current\n    val lifecycleOwner = LocalLifecycleOwner.current", replacement.strip())

with open(file_path, "w") as f:
    f.write(content)
