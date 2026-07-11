with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("""                        com.example.ui.screens.settings.SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )""", """                        com.example.ui.screens.settings.SettingsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            themeManager = appContainer.themeManager
                        )""")

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun SettingsScreen(\n    onNavigateBack: () -> Unit\n)", "fun SettingsScreen(\n    onNavigateBack: () -> Unit,\n    themeManager: com.example.ThemeManager\n)")
content = content.replace("var darkTheme by remember { mutableStateOf(true) }", "val darkTheme by themeManager.isDarkTheme.androidx.compose.runtime.collectAsState()")
content = content.replace("onCheckedChange = { darkTheme = it }", "onCheckedChange = { themeManager.setDarkTheme(it) }")

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
