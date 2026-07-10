import re

file_path = "app/src/main/java/com/example/viewmodels/DashboardViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("userRepository.getStreak()", "userRepository.validateAndGetStreak()")

with open(file_path, "w") as f:
    f.write(content)
