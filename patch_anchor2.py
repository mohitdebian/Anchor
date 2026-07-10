import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    "fun AnchorNavigation(appContainer: AppContainer, initialBlockedApp: String? = null) {",
    "fun AnchorNavigation(appContainer: AppContainer, initialBlockedApp: String? = null, onClearBlockedApp: () -> Unit = {}) {"
)

replacement = """
        com.example.ui.screens.block.BlockedOverlayScreen(
            packageName = currentBlockedApp ?: "",
            onGoHome = {
                currentBlockedApp = null
                onClearBlockedApp()
            }
        )
"""

content = re.sub(r'com\.example\.ui\.screens\.block\.BlockedOverlayScreen\([\s\S]*?onGoHome = \{[\s\S]*?currentBlockedApp = null\s*\}\s*\)', replacement.strip(), content)

with open(file_path, "w") as f:
    f.write(content)
