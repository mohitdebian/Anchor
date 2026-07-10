import re

file_path = "app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Add Screen Time Card logic
screen_time_card = """
            val h = uiState.screenTimeMinutes / 60
            val m = uiState.screenTimeMinutes % 60
            val screenTimeStr = if (h > 0) "${h}h ${m}m" else "${m}m"
            val displayTime = if (!uiState.hasUsagePermission) "Needs Permission" else screenTimeStr

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!uiState.hasUsagePermission) {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            androidx.compose.ui.platform.LocalContext.current.startActivity(intent)
                        }
                    }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SCREEN TIME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            Icons.Default.PhoneIphone,
                            contentDescription = "Screen Time",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = displayTime,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
"""

# Insert it before "Weekly Trend"
content = content.replace('Text(\n                text = "Weekly Trend"', screen_time_card.strip() + '\n\n            Text(\n                text = "Weekly Trend"')

# Make sure imports are there:
if "import androidx.compose.foundation.clickable" not in content:
    content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.clickable\nimport androidx.compose.ui.unit.sp\nimport androidx.compose.material.icons.filled.PhoneIphone")

with open(file_path, "w") as f:
    f.write(content)
