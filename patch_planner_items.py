import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

new_item_code = """
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF161618))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = schedule.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "${schedule.startTime} - ${schedule.endTime}", color = Color.LightGray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                val blockedCount = if (schedule.blockedPackages.isBlank()) 0 else schedule.blockedPackages.split(",").size
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (blockedCount > 0) Color.Red else Color.Gray))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "$blockedCount apps blocked", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
"""

content = re.sub(
    r'Row\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.padding\(vertical = 8\.dp\)\s*\.clip\(RoundedCornerShape\(16\.dp\)\).*?height\(IntrinsicSize\.Min\)\s*\).*?\}\s*\}\s*\}',
    new_item_code.strip(),
    content,
    flags=re.DOTALL
)

with open(file_path, "w") as f:
    f.write(content)
