import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    """fun DashboardScreen(
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
)""",
    """fun DashboardScreen(
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
)"""
)

content = content.replace(
    'IconButton(onClick = { /* TODO: Settings */ })',
    'IconButton(onClick = onNavigateToSettings)'
)
content = content.replace(
    'IconButton(onClick = { /* TODO */ })',
    'IconButton(onClick = onNavigateToSettings)'
)

with open(file_path, "w") as f:
    f.write(content)
