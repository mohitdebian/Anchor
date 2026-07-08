import re

with open('app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt', 'r') as f:
    content = f.read()

pattern = r"DashboardViewModel\(\s*anchorApplication\(\)\.container\.focusRepository,\s*anchorApplication\(\)\.container\.usageStatsRepository\s*\)"
replacement = r"""DashboardViewModel(
                anchorApplication().container.focusRepository,
                anchorApplication().container.usageStatsRepository,
                anchorApplication().container.userRepository
            )"""

new_content = re.sub(pattern, replacement, content)
with open('app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt', 'w') as f:
    f.write(new_content)
