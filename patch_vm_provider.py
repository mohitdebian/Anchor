import re

file_path = "app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("anchorApplication().container.nvidiaNimApi", "anchorApplication().container.geminiApiService")

with open(file_path, "w") as f:
    f.write(content)
