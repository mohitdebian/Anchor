import re

with open('app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt', 'r') as f:
    content = f.read()

pattern = r"Text\(\s*val currentHour = java\.util\.Calendar\.getInstance\(\)\.get\(java\.util\.Calendar\.HOUR_OF_DAY\)\s*val greeting = when \(currentHour\) \{\s*in 5\.\.11 -> \"Good morning\"\s*in 12\.\.16 -> \"Good afternoon\"\s*in 17\.\.20 -> \"Good evening\"\s*else -> \"Good night\"\s*\}\s*text = \"\$greeting, \$\{uiState\.userName \?: \"there\"\}\.\","

replacement = r"""val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val greeting = when (currentHour) {
                    in 5..11 -> "Good morning"
                    in 12..16 -> "Good afternoon"
                    in 17..20 -> "Good evening"
                    else -> "Good night"
                }
                Text(
                    text = "$greeting, ${uiState.userName ?: "there"}.","""

new_content = re.sub(pattern, replacement, content)
with open('app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt', 'w') as f:
    f.write(new_content)
