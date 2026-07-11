with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("""PledgeScreen(
                            onFinish = {
                                navController.navigate("dashboard") {
                                    popUpTo("pledge") { inclusive = true }
                                }
                            },
                            onSkip = {
                                navController.navigate("dashboard") {
                                    popUpTo("pledge") { inclusive = true }
                                }
                            }
                        )""", """PledgeScreen(
                            onFinish = {
                                androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("onboarding_complete", true).apply()
                                navController.navigate("dashboard") {
                                    popUpTo("pledge") { inclusive = true }
                                }
                            },
                            onSkip = {
                                androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("onboarding_complete", true).apply()
                                navController.navigate("dashboard") {
                                    popUpTo("pledge") { inclusive = true }
                                }
                            }
                        )""")

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "w") as f:
    f.write(content)
