import re

file_path = "app/src/main/java/com/example/viewmodels/AnalyticsViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = re.sub(r'\}\s*\}\s*$', '}', content)

with open(file_path, "w") as f:
    f.write(content)
