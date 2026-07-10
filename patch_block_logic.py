import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                if (currentApp != null && currentApp != packageName) {
                    var isBlocked = sharedPreferences.getBoolean(currentApp, false)
                    
                    if (!isBlocked) {
"""

content = re.sub(
    r'if \(currentApp != null && currentApp != packageName\) \{[\s\S]*?if \(!isBlocked\) \{',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
