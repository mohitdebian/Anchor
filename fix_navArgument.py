import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'arguments = listOf(androidx.navigation.navArgument("pkg") { nullable = true })',
    'arguments = listOf(androidx.navigation.navArgument("pkg") { type = androidx.navigation.NavType.StringType; nullable = true })'
)

with open(file_path, "w") as f:
    f.write(content)
