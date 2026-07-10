import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'AnchorNavigation(appContainer = appContainer)',
    'val blockedApp = intent.getStringExtra("blocked_app_package")\n            AnchorNavigation(appContainer = appContainer, initialBlockedApp = blockedApp)'
)

with open(file_path, "w") as f:
    f.write(content)

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'fun AnchorNavigation(appContainer: AppContainer)',
    'fun AnchorNavigation(appContainer: AppContainer, initialBlockedApp: String? = null)'
)

content = content.replace(
    'startDestination = "onboarding"',
    'startDestination = if (initialBlockedApp != null) "blocked_overlay?pkg=$initialBlockedApp" else "onboarding"'
)

add_blocked_route = """
                composable(
                    route = "blocked_overlay?pkg={pkg}",
                    arguments = listOf(androidx.navigation.navArgument("pkg") { nullable = true })
                ) { backStackEntry ->
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
                        val pkg = backStackEntry.arguments?.getString("pkg") ?: ""
                        com.example.ui.screens.block.BlockedOverlayScreen(
                            packageName = pkg,
                            onGoHome = {
                                navController.navigate("dashboard") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
"""

content = content.replace(
    'composable("block") {',
    add_blocked_route.strip() + '\n                composable("block") {'
)

with open(file_path, "w") as f:
    f.write(content)
