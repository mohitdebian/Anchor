import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

header_replacement = """
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your Planner",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Text(
                text = "Schedule blocks of deep work to maintain consistent focus.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
"""

content = re.sub(
    r'Spacer\(modifier = Modifier.height\(24.dp\)\)\s*Text\(\s*text = fullDateFormat\.format\(selectedDate\).*?Spacer\(modifier = Modifier.height\(16.dp\)\)',
    header_replacement,
    content,
    flags=re.DOTALL
)

with open(file_path, "w") as f:
    f.write(content)
