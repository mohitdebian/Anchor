import re

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "r") as f:
    content = f.read()

pattern = r'AsyncImage\(\s*model = "https://media\.giphy\.com/media/ASd0Ukj0y3qMM/giphy\.gif"[^\)]+\)'

content = re.sub(pattern, "", content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "w") as f:
    f.write(content)
