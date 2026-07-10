import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("Icons.Default.Edit", "androidx.compose.material.icons.filled.Edit")
content = content.replace("Icons.Default.Delete", "androidx.compose.material.icons.filled.Delete")

with open(file_path, "w") as f:
    f.write(content)
