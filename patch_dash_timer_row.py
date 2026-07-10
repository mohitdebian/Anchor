import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable { onNavigateToBlocks() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Block, contentDescription = "Blocked Apps", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${uiState.blockedAppsCount} apps blocked",
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            }
"""

content = re.sub(r'Row\([\s\S]*?text = "\$\{uiState.blockedAppsCount\} apps blocked"[\s\S]*?fontWeight = FontWeight.Medium\n\s*\)\n\s*\}', replacement.strip(), content)

with open(file_path, "w") as f:
    f.write(content)
