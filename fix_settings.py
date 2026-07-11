with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

if "import androidx.compose.runtime.collectAsState" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.collectAsState")

content = content.replace("themeManager.isDarkTheme.androidx.compose.runtime.collectAsState()", "themeManager.isDarkTheme.collectAsState()")

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
