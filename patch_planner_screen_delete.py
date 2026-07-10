import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = schedule.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${schedule.startTime} - ${schedule.endTime}", color = Color.Gray, fontSize = 14.sp)
                                }
                                IconButton(onClick = { viewModel.deleteSchedule(schedule) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                }
"""

content = re.sub(
    r'Column \{\s*Text\(text = schedule\.title, color = Color\.White, fontSize = 16\.sp, fontWeight = FontWeight\.Bold\)\s*Spacer\(modifier = Modifier\.height\(4\.dp\)\)\s*Text\(text = "\$\{schedule\.startTime\} - \$\{schedule\.endTime\}", color = Color\.Gray, fontSize = 14\.sp\)\s*\}',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
