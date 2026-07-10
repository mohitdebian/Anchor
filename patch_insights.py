import re

file_path = "app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("Powered by Google Gemini ✨", "Powered by NVIDIA NIM ✨")
content = content.replace("Gemini is analyzing your focus...", "NVIDIA NIM is analyzing your focus...")
content = content.replace("Gemini API Error", "NIM API Error")
content = content.replace("Gemini API", "NIM API")

with open(file_path, "w") as f:
    f.write(content)
