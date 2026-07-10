import re

file_path = "app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Gemini is analyzing your focus...", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else if
"""

content = re.sub(
    r'AnimatedVisibility\([\s\S]*?\} else if',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
