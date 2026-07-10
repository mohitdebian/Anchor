import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    val navController = rememberNavController()
    var currentBlockedApp by remember { mutableStateOf(initialBlockedApp) }
    
    androidx.compose.runtime.LaunchedEffect(initialBlockedApp) {
        if (initialBlockedApp != null) {
            currentBlockedApp = initialBlockedApp
        }
    }
"""

content = content.replace("    val navController = rememberNavController()\nvar currentBlockedApp by remember { mutableStateOf(initialBlockedApp) }", replacement.strip())

with open(file_path, "w") as f:
    f.write(content)
