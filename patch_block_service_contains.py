import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'if (activeSchedule.blockedPackages.contains(currentApp)) {',
    'if (activeSchedule.blockedPackages.split(",").contains(currentApp)) {'
)

with open(file_path, "w") as f:
    f.write(content)
