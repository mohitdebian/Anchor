with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "r") as f:
    content = f.read()

content = content.replace('startDestination = "onboarding"', """startDestination = if (androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE).getBoolean("onboarding_complete", false)) "dashboard" else "onboarding\"""")

with open("app/src/main/java/com/example/navigation/AnchorNavigation.kt", "w") as f:
    f.write(content)
