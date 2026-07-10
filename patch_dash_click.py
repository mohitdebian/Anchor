import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clickable { onNavigateToBlocks() }
                                    .padding(8.dp)
                            ) {
"""

content = content.replace("""                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {""", replacement.strip())

with open(file_path, "w") as f:
    f.write(content)
