import re

with open("app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "r") as f:
    content = f.read()

imports = """
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.os.Build
"""
if "import coil.compose.AsyncImage" not in content:
    content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\n" + imports)

pattern1 = r'AndroidView\(\s*factory = \{ ctx ->\s*WebView\(ctx\)\.apply \{\s*settings\.javaScriptEnabled = true\s*setBackgroundColor\(android\.graphics\.Color\.TRANSPARENT\)\s*loadDataWithBaseURL\(null, \"\"\"[^"]*<img src="(https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif)" />[^"]*\"\"\", "text/html", "utf-8", null\)\s*\}\s*\},\s*modifier = (Modifier\.fillMaxWidth\(\)\.height\(150\.dp\))\s*\)'

replacement1 = r'''AsyncImage(
                    model = "\1",
                    imageLoader = ImageLoader.Builder(context).components {
                        if (Build.VERSION.SDK_INT >= 28) {
                            add(ImageDecoderDecoder.Factory())
                        } else {
                            add(GifDecoder.Factory())
                        }
                    }.build(),
                    contentDescription = "Welcome GIF",
                    modifier = \2
                )'''

content = re.sub(pattern1, replacement1, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "r") as f:
    content2 = f.read()

if "import coil.compose.AsyncImage" not in content2:
    content2 = content2.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\n" + imports)

pattern2 = r'AndroidView\(\s*factory = \{ ctx ->\s*WebView\(ctx\)\.apply \{\s*settings\.javaScriptEnabled = true\s*setBackgroundColor\(android\.graphics\.Color\.TRANSPARENT\)\s*loadDataWithBaseURL\(null, \"\"\"[^"]*<img src="(https://media.giphy.com/media/ASd0Ukj0y3qMM/giphy.gif)" />[^"]*\"\"\", "text/html", "utf-8", null\)\s*\}\s*\},\s*modifier = (Modifier\.fillMaxWidth\(\)\.height\(120\.dp\))\s*\)'

replacement2 = r'''AsyncImage(
            model = "\1",
            imageLoader = ImageLoader.Builder(androidx.compose.ui.platform.LocalContext.current).components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }.build(),
            contentDescription = "Cute Star GIF",
            modifier = \2
        )'''

content2 = re.sub(pattern2, replacement2, content2, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "w") as f:
    f.write(content2)
