import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("        SharedTransitionLayout {\n        SharedTransitionLayout {", "        SharedTransitionLayout {")

with open(file_path, "w") as f:
    f.write(content)
