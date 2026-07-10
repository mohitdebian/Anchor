file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

st_start = content.find("// Screen Time and Blocked Alerts Cards")
st_end = content.find("if (!uiState.isTimerActive) {")

if st_start != -1 and st_end != -1:
    content = content[:st_start] + content[st_end:]

ra_start = content.find("// Recent Activity Header")
ra_end = content.find("Spacer(modifier = Modifier.height(32.dp))", ra_start)
if ra_start != -1 and ra_end != -1:
    content = content[:ra_start] + content[ra_end:]

with open(file_path, "w") as f:
    f.write(content)
