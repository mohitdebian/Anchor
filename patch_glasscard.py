with open("app/src/main/java/com/example/ui/components/GlassCard.kt", "r") as f:
    content = f.read()

content = content.replace(".background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))", ".background(androidx.compose.ui.graphics.Color(0xFF132018).copy(alpha = 0.75f))")
content = content.replace(".border(\n                width = 1.dp,\n                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),", ".border(\n                width = 1.dp,\n                color = androidx.compose.ui.graphics.Color(0xFF10B981).copy(alpha = 0.15f),")

with open("app/src/main/java/com/example/ui/components/GlassCard.kt", "w") as f:
    f.write(content)
