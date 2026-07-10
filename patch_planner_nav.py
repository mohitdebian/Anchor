import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'onNavigateToAddSchedule = { navController.navigate("add_schedule") }',
    'onNavigateToAddSchedule = { date -> navController.navigate("add_schedule/$date") }'
)

content = content.replace(
    'composable("add_schedule") {',
    'composable("add_schedule/{date}", arguments = listOf(androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType })) { backStackEntry ->\n                    val dateStr = backStackEntry.arguments?.getString("date") ?: ""'
)

content = content.replace(
    'com.example.ui.screens.planner.AddScheduleScreen(\n                            onNavigateBack = { navController.popBackStack() }\n                        )',
    'com.example.ui.screens.planner.AddScheduleScreen(\n                            dateStr = dateStr,\n                            onNavigateBack = { navController.popStack() }\n                        )'.replace('popStack', 'popBackStack')
)

with open(file_path, "w") as f:
    f.write(content)
