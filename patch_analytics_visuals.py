import re

file_path = "app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
@Composable
fun WeeklyChart(data: List<Float>) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val surfaceColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        
        // Animation for the bars
        var animationPlayed by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        val animationProgress by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (animationPlayed) 1f else 0f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        )
        
        androidx.compose.runtime.LaunchedEffect(key1 = true) {
            animationPlayed = true
        }
        
        Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            val barWidth = (size.width / data.size) * 0.6f
            val spacing = (size.width - (barWidth * data.size)) / (data.size - 1)
            
            data.forEachIndexed { index, value ->
                val x = index * (barWidth + spacing)
                val targetHeight = size.height * value
                val barHeight = targetHeight * animationProgress
                val y = size.height - barHeight
                
                // Background bar
                drawRoundRect(
                    color = surfaceColor,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(barWidth / 2)
                )
                
                // Value bar
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2)
                )
            }
        }
    }
}
"""

content = re.sub(
    r'@Composable\s*fun WeeklyChart\(data: List<Float>\) \{[\s\S]*?\}\s*\}',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
