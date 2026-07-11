with open("app/src/main/java/com/example/ui/theme/Color.kt", "r") as f:
    content = f.read()

content = content.replace("val PrimaryAccent = Color(0xFF3B82F6) // Sleek Blue Glow", "val PrimaryAccent = Color(0xFF10B981)")
content = content.replace("val SecondaryAccent = Color(0xFF1E293B) // Slate 800", "val SecondaryAccent = Color(0xFF163321)")
content = content.replace("val TertiaryAccent = Color(0xFF334155) // Slate 700", "val TertiaryAccent = Color(0xFF1D3E25)")

content = content.replace("val BackgroundDark = Color(0xFF020617) // Slate 950", "val BackgroundDark = Color(0xFF121212)")
content = content.replace("val SurfaceDark = Color(0xFF0F172A) // Slate 900", "val SurfaceDark = Color(0xFF1C1C1E)")
content = content.replace("val SurfaceVariantDark = Color(0xFF1E293B) // Slate 800", "val SurfaceVariantDark = Color(0xFF2C2C2E)")

with open("app/src/main/java/com/example/ui/theme/Color.kt", "w") as f:
    f.write(content)
