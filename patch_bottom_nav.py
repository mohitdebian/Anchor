with open("app/src/main/java/com/example/ui/components/BottomNavigationBar.kt", "r") as f:
    content = f.read()

content = content.replace("Color(0xFF1C1C1E)", "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant")
content = content.replace("Color(0xFF163321)", "androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer")
content = content.replace("Color(0xFF10B981)", "androidx.compose.material3.MaterialTheme.colorScheme.primary")
content = content.replace("Color(0xFF8E8E93)", "androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant")

with open("app/src/main/java/com/example/ui/components/BottomNavigationBar.kt", "w") as f:
    f.write(content)
