import re

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "r") as f:
    content = f.read()

analytics_pattern = re.compile(r'composable\("analytics"\) \{.*?onNavigateToAIStats = \{ navController\.navigate\("insights"\) \{ popUpTo\("dashboard"\) \} \},\n\s*\)\n\s*\}\n\s*\) \{ padding ->\n\s*androidx\.compose\.foundation\.layout\.Box\(modifier = androidx\.compose\.ui\.Modifier\.fillMaxSize\(\)\.padding\(padding\)\) \{\n\s*com\.example\.ui\.screens\.analytics\.AnalyticsScreen\(\n\s*onNavigateBack = \{ navController\.popBackStack\(\) \}\n\s*\)\n\s*\}\n\s*\}\n\s*\}\n\s*\}', re.DOTALL)

analytics_replacement = """composable("analytics") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.analytics.AnalyticsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                            onNavigateToPlanner = { navController.navigate("planner") { popUpTo("dashboard") } },
                            onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            onNavigateToAnalytics = { },
                            onNavigateToAIStats = { navController.navigate("insights") { popUpTo("dashboard") } }
                        )
                    }
                }"""

content = analytics_pattern.sub(analytics_replacement, content)

insights_pattern = re.compile(r'composable\("insights"\) \{.*?onNavigateToAIStats = \{ \}\n\s*\)\n\s*\}\n\s*\) \{ padding ->\n\s*androidx\.compose\.foundation\.layout\.Box\(modifier = androidx\.compose\.ui\.Modifier\.fillMaxSize\(\)\.padding\(padding\)\) \{\n\s*com\.example\.ui\.screens\.insights\.InsightsScreen\(\n\s*onNavigateBack = \{ navController\.popBackStack\(\) \}\n\s*\)\n\s*\}\n\s*\}\n\s*\}\n\s*\}', re.DOTALL)

insights_replacement = """composable("insights") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        com.example.ui.screens.insights.InsightsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToFocus = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = true } } },
                            onNavigateToPlanner = { navController.navigate("planner") { popUpTo("dashboard") } },
                            onNavigateToBlocks = { navController.navigate("block") { popUpTo("dashboard") } },
                            onNavigateToAnalytics = { navController.navigate("analytics") { popUpTo("dashboard") } },
                            onNavigateToAIStats = { }
                        )
                    }
                }"""

content = insights_pattern.sub(insights_replacement, content)

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "w") as f:
    f.write(content)
