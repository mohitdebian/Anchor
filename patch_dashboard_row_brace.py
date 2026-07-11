with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    new_lines.append(line)
    if 'Text("Stop")' in line:
        if i + 1 < len(lines) and '}' in lines[i+1]:
            # This is the line with }
            pass
    if 'Spacer(modifier = Modifier.height(16.dp))' in line and i > 0 and '}' in lines[i-1] and 'Text("Stop")' in lines[i-3]:
        # We need to insert a } before this Spacer
        new_lines.insert(-1, "                            }\n")

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "w") as f:
    f.writelines(new_lines)
