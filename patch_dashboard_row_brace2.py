with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "r") as f:
    content = f.read()

target = """                                GlowingButton(
                                    onClick = { viewModel.stopTimer((context.applicationContext as com.example.AnchorApplication).container.ambientSoundManager) },
                                    modifier = Modifier.weight(1f),
                                    color = Color.Red.copy(alpha = 0.7f),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Stop")
                                }
                            Spacer(modifier = Modifier.height(16.dp))
                            val ambientSoundManager = (context.applicationContext as com.example.AnchorApplication).container.ambientSoundManager"""

replacement = """                                GlowingButton(
                                    onClick = { viewModel.stopTimer((context.applicationContext as com.example.AnchorApplication).container.ambientSoundManager) },
                                    modifier = Modifier.weight(1f),
                                    color = Color.Red.copy(alpha = 0.7f),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Stop")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            val ambientSoundManager = (context.applicationContext as com.example.AnchorApplication).container.ambientSoundManager"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "w") as f:
    f.write(content)
