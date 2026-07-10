import re

file_path = "app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("Powered by NVIDIA NIM", "Powered by Google Gemini ✨")

with open(file_path, "w") as f:
    f.write(content)
