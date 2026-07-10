import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                composable(
                    "add_schedule/{date}?scheduleId={scheduleId}",
                    arguments = listOf(
                        androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("scheduleId") { type = androidx.navigation.NavType.IntType; defaultValue = -1 }
                    )
                ) { backStackEntry ->
                    val dateStr = backStackEntry.arguments?.getString("date") ?: ""
                    val scheduleId = backStackEntry.arguments?.getInt("scheduleId") ?: -1
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.planner.AddScheduleScreen(
                            dateStr = dateStr,
                            scheduleId = scheduleId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
"""

content = re.sub(
    r'composable\("add_schedule/\{date\}"[\s\S]*?onNavigateBack = \{ navController\.popBackStack\(\) \}\n\s*\)\n\s*\}\n\s*\}',
    replacement.strip(),
    content
)

content = content.replace(
    'onNavigateToAddSchedule = { date -> navController.navigate("add_schedule/$date") }',
    'onNavigateToAddSchedule = { date -> navController.navigate("add_schedule/$date") }'
)

with open(file_path, "w") as f:
    f.write(content)
