import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                if (currentApp != null && currentApp != packageName && !isHomeApp(currentApp)) {
                    var isBlocked = sharedPreferences.getBoolean(currentApp, false)
"""

content = content.replace(
    'if (currentApp != null && currentApp != packageName) {\n                    var isBlocked = sharedPreferences.getBoolean(currentApp, false)',
    replacement.strip()
)

is_home_func = """
    private fun isHomeApp(packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }
"""

content = content.replace('    private fun isTimeBetween', is_home_func + '\n    private fun isTimeBetween')

with open(file_path, "w") as f:
    f.write(content)
