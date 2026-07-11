with open("app/src/main/java/com/example/AppContainer.kt", "r") as f:
    content = f.read()

theme_manager = """
class ThemeManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isDarkTheme = kotlinx.coroutines.flow.MutableStateFlow(prefs.getBoolean("dark_theme", true))
    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("dark_theme", isDark).apply()
        isDarkTheme.value = isDark
    }
}
"""

if "class ThemeManager" not in content:
    content = content.replace("interface AppContainer {", theme_manager + "\ninterface AppContainer {\n    val themeManager: ThemeManager")
    content = content.replace("override val geminiApiService", "override val themeManager: ThemeManager by lazy { ThemeManager(context) }\n\n    override val geminiApiService")

with open("app/src/main/java/com/example/AppContainer.kt", "w") as f:
    f.write(content)
