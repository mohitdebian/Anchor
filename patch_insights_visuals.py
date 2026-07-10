import re

file_path = "app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

imports = """
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
"""

content = content.replace("import androidx.compose.ui.Modifier", imports + "\nimport androidx.compose.ui.Modifier")

replacement_loading = """
                    AnimatedVisibility(
                        visible = uiState.isLoading,
                        enter = fadeIn(animationSpec = tween(500))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Gemini is analyzing your focus...", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
"""

content = re.sub(
    r'if \(uiState\.isLoading\) \{.*?\}\s*\} else if',
    replacement_loading.strip() + ' else if',
    content,
    flags=re.DOTALL
)

with open(file_path, "w") as f:
    f.write(content)
