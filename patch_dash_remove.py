import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Remove the Screen Time and Blocked Alerts Cards
content = re.sub(r'// Screen Time and Blocked Alerts Cards[\s\S]*?StyledGlassCard\([\s\S]*?\}\s*\}\s*\}\s*\}\s*\}\s*Spacer\(modifier = Modifier\.height\(28\.dp\)\)', 'Spacer(modifier = Modifier.height(28.dp))', content)

# Remove Recent Activity
content = re.sub(r'// Recent Activity Header[\s\S]*?\}\s*\}\s*\}\s*\}\s*Spacer\(modifier = Modifier\.height\(32\.dp\)\)', 'Spacer(modifier = Modifier.height(32.dp))', content)

with open(file_path, "w") as f:
    f.write(content)
