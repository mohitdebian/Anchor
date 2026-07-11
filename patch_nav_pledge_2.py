with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("""composable("pledge") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        PledgeScreen(
                            onFinish = {
                                androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("onboarding_complete", true).apply()""", """composable("pledge") {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        PledgeScreen(
                            onFinish = {
                                context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("onboarding_complete", true).apply()""")

content = content.replace("""onSkip = {
                                androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("onboarding_complete", true).apply()""", """onSkip = {
                                context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).edit().putBoolean("onboarding_complete", true).apply()""")

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "w") as f:
    f.write(content)
