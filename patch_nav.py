import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

add_schedule_route = """
                composable("add_schedule") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.planner.AddScheduleScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
"""

content = content.replace(
    'composable("block") {',
    add_schedule_route.strip() + '\n                composable("block") {'
)

with open(file_path, "w") as f:
    f.write(content)
