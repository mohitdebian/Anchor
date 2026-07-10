import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    "AnchorNavigation(appContainer = appContainer, initialBlockedApp = blockedApp)",
    "AnchorNavigation(appContainer = appContainer, initialBlockedApp = blockedApp, onClearBlockedApp = { _blockedAppIntent.value = null })"
)

with open(file_path, "w") as f:
    f.write(content)
