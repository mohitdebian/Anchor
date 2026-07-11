with open("app/src/main/java/com/example/ui/components/BottomNavigationBar.kt", "r") as f:
    content = f.read()

content = content.replace("androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant", "Color(0xFF1C1C1E)")
content = content.replace("androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer", "Color(0xFF163321)")
content = content.replace("androidx.compose.material3.MaterialTheme.colorScheme.primary", "Color(0xFF10B981)")
content = content.replace("androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant", "Color(0xFF8E8E93)")

with open("app/src/main/java/com/example/ui/components/BottomNavigationBar.kt", "w") as f:
    f.write(content)
