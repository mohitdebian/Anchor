import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

# Revert previous incorrect additions
content = content.replace("                            onNavigateToSettings = { navController.navigate(\"settings\") }", "")
content = content.replace(",\n\n                        )", "\n                        )")
content = content.replace(",\n                        )", "\n                        )")

# Add it correctly ONLY to DashboardScreen
replacement = """
                        DashboardScreen(
                            onNavigateToPlanner = { navController.navigate("planner") { popUpTo("dashboard") } },
                            onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            
                            onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                            onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
"""
content = re.sub(
    r'DashboardScreen\([\s\S]*?onNavigateToAIStats = \{ navController\.navigate\("insights"\) \{ popUpTo\("dashboard"\) \} \}\s*\)',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
