import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'startForeground(1, notification)',
    'try { startForeground(1, notification) } catch (e: Exception) { e.printStackTrace() }'
)

with open(file_path, "w") as f:
    f.write(content)
