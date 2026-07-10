import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

# First replace all occurrences of onNavigateToAddSchedule to empty string (clean slate)
content = re.sub(r'\s*onNavigateToAddSchedule = \{.*?\},?', '', content)

# Then insert it only in PlannerScreen
planner_screen = """
                        com.example.ui.screens.planner.PlannerScreen(
                            onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                            onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            onNavigateToAddSchedule = { navController.navigate("add_schedule") },
                            onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                            onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } }
                        )
"""
content = re.sub(r'com\.example\.ui\.screens\.planner\.PlannerScreen\(.*?onNavigateToAIStats = \{ navController\.navigate\("insights"\) \{ popUpTo\("dashboard"\) \} \}\s*\)', planner_screen.strip(), content, flags=re.DOTALL)

with open(file_path, "w") as f:
    f.write(content)
