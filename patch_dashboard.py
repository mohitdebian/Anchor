import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

schedule_ui = """
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.todaySchedules.isEmpty()) {
                    Text(
                        text = "No schedules for today.",
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                } else {
                    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.todaySchedules.forEach { schedule ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF132018).copy(alpha = 0.75f))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = schedule.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = schedule.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${schedule.startTime} - ${schedule.endTime}", color = Color.LightGray, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
"""

content = content.replace(
    '// Streak Card',
    schedule_ui + '\n                                // Streak Card'
)

with open(file_path, "w") as f:
    f.write(content)
