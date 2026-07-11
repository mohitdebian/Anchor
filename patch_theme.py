with open("app/src/main/java/com/example/ui/theme/Color.kt", "r") as f:
    content = f.read()

light_colors = """
// Light Backgrounds
val BackgroundLight = Color(0xFFF8FAFC) // Slate 50
val SurfaceLight = Color(0xFFFFFFFF) // White
val SurfaceVariantLight = Color(0xFFE2E8F0) // Slate 200

// Light Text
val TextPrimaryLight = Color(0xFF0F172A) // Slate 900
val TextSecondaryLight = Color(0xFF475569) // Slate 600
"""

if "BackgroundLight" not in content:
    content = content + "\n" + light_colors

with open("app/src/main/java/com/example/ui/theme/Color.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "r") as f:
    content = f.read()

light_scheme = """private val LightColorScheme = lightColorScheme(
    primary = PrimaryAccent,
    secondary = SecondaryAccent,
    tertiary = TertiaryAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    error = ErrorRed,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = Color.White
)"""

content = content.replace("private val LightColorScheme = DarkColorScheme // Force dark theme", light_scheme)
content = content.replace("fun AnchorTheme(\n  darkTheme: Boolean = true, // Always dark", "fun AnchorTheme(\n  darkTheme: Boolean = true,")
content = content.replace("val colorScheme = DarkColorScheme", "val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme")

with open("app/src/main/java/com/example/ui/theme/Theme.kt", "w") as f:
    f.write(content)
