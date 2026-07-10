import re

file_path = "app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'DashboardViewModel(\n                anchorApplication().container.focusRepository,',
    'DashboardViewModel(\n                anchorApplication(),\n                anchorApplication().container.focusRepository,'
)

with open(file_path, "w") as f:
    f.write(content)
