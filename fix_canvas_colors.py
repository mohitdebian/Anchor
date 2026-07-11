import os

for path in ["app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt"]:
    with open(path, "r") as f:
        content = f.read()
    
    content = content.replace("androidx.compose.material3.MaterialTheme.colorScheme.onBackground", "Color.White")
    
    with open(path, "w") as f:
        f.write(content)
