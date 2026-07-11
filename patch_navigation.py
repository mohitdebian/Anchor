with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "r") as f:
    content = f.read()

pattern = """    val navController = rememberNavController()
var currentBlockedApp by remember { mutableStateOf(initialBlockedApp) }"""

replacement = """    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasCompletedOnboarding = remember {
        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("onboarding_complete", false)
    }
var currentBlockedApp by remember { mutableStateOf(initialBlockedApp) }"""

content = content.replace(pattern, replacement)

pattern2 = """            NavHost(
                navController = navController,
                startDestination = "onboarding",
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),"""

replacement2 = """            NavHost(
                navController = navController,
                startDestination = if (hasCompletedOnboarding) "dashboard" else "onboarding",
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),"""

content = content.replace(pattern2, replacement2)

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "w") as f:
    f.write(content)
