import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                    if (isBlocked) {
                        if (android.provider.Settings.canDrawOverlays(this@BlockService)) {
                            val intent = Intent(this@BlockService, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                putExtra("blocked_app_package", currentApp)
                            }
                            startActivity(intent)
                        } else {
                            // Fallback if overlay permission is missing
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                        }
                        // Sleep a bit to prevent multiple intents
                        delay(2000)
                    }
"""

content = re.sub(
    r'if \(isBlocked\) \{[\s\S]*?delay\(2000\)\n\s*\}',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
