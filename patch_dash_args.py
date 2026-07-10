import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """fun DashboardScreen(
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel(factory = com.example.viewmodels.AppViewModelProvider.Factory)
)"""

content = re.sub(r'fun DashboardScreen\([\s\S]*?viewModel: DashboardViewModel = viewModel\(factory = com\.example\.viewmodels\.AppViewModelProvider\.Factory\)\s*\)', replacement.strip(), content)

with open(file_path, "w") as f:
    f.write(content)
