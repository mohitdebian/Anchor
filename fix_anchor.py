import re

with open('app/src/main/java/com/example/navigation/AnchorNavigation.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if "onNavigateToBlocks" in line:
        if i > 0 and "onNavigateToBlocks" in lines[i-1]:
            continue
    new_lines.append(line)

with open('app/src/main/java/com/example/navigation/AnchorNavigation.kt', 'w') as f:
    f.writelines(new_lines)
