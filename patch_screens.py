with open("app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun AnalyticsScreen(\n    onNavigateBack: () -> Unit,\n    viewModel: AnalyticsViewModel", """fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    viewModel: AnalyticsViewModel""")

content = content.replace("containerColor = Color(0xFF121212)\n    ) { padding ->", """containerColor = Color(0xFF121212),
        bottomBar = {
            com.example.ui.components.BottomNavigationBar(
                currentRoute = "analytics",
                onNavigateToFocus = onNavigateToFocus,
                onNavigateToPlanner = onNavigateToPlanner,
                onNavigateToBlocks = onNavigateToBlocks,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToAIStats = onNavigateToAIStats
            )
        }
    ) { padding ->""")

content = content.replace('title = { Text("Analytics") }', 'title = { Text("Analytics", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 20.sp, color = androidx.compose.ui.graphics.Color.White) }')
content = content.replace('Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")', 'Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = androidx.compose.ui.graphics.Color.White)')

if "import androidx.compose.ui.unit.sp" not in content:
    content = content.replace("import androidx.compose.ui.unit.dp", "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp")

with open("app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun InsightsScreen(\n    onNavigateBack: () -> Unit,\n    viewModel: InsightsViewModel", """fun InsightsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    viewModel: InsightsViewModel""")

content = content.replace("containerColor = Color(0xFF121212)\n    ) { padding ->", """containerColor = Color(0xFF121212),
        bottomBar = {
            com.example.ui.components.BottomNavigationBar(
                currentRoute = "insights",
                onNavigateToFocus = onNavigateToFocus,
                onNavigateToPlanner = onNavigateToPlanner,
                onNavigateToBlocks = onNavigateToBlocks,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToAIStats = onNavigateToAIStats
            )
        }
    ) { padding ->""")

content = content.replace('title = { Text("AI Insights", fontWeight = FontWeight.Bold) }', 'title = { Text("AI Insights", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = androidx.compose.ui.graphics.Color.White) }')
content = content.replace('Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")', 'Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = androidx.compose.ui.graphics.Color.White)')

with open("app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt", "w") as f:
    f.write(content)

