import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'override fun onNewIntent(intent: android.content.Intent?) {',
    'override fun onNewIntent(intent: android.content.Intent) {'
)

with open(file_path, "w") as f:
    f.write(content)
