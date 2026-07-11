import os

for root, dirs, files in os.walk("app/src/main/java/com/example/ui/screens"):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            content = content.replace("color = Color.White", "color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground")
            content = content.replace("color = androidx.compose.ui.graphics.Color.White", "color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground")
            
            content = content.replace("tint = Color.White", "tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground")
            content = content.replace("tint = androidx.compose.ui.graphics.Color.White", "tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground")
            
            content = content.replace("tint = Color.LightGray", "tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant")
            content = content.replace("color = Color.LightGray", "color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant")
            
            content = content.replace("color = Color.Gray", "color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant")
            
            content = content.replace("Color(0xFF2C2C2E)", "androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant")
            content = content.replace("Color(0xFF1C1C1E)", "androidx.compose.material3.MaterialTheme.colorScheme.surface")
            
            with open(path, "w") as f:
                f.write(content)
