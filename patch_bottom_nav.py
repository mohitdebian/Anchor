import re

file_path = "app/src/main/java/com/example/ui/components/BottomNavigationBar.kt"
with open(file_path, "r") as f:
    content = f.read()

content = re.sub(r'NavigationBarItem\([\s\S]*?onClick = onNavigateToBlocks\s*\)', '', content)

with open(file_path, "w") as f:
    f.write(content)
