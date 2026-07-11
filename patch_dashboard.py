import re

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "r") as f:
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

# We want to replace the AndroidView logic. Let's just find and replace the exact blocks.
pattern1 = r'AndroidView\(\s*factory = \{ ctx ->\s*WebView\(ctx\)\.apply \{\s*settings\.javaScriptEnabled = true\s*setBackgroundColor\(android\.graphics\.Color\.TRANSPARENT\)\s*loadDataWithBaseURL\(null, \"\"\"[^"]*<img src="(https://media.giphy.com/media/13HgwGsXF0aiGY/giphy.gif)" />[^"]*\"\"\", "text/html", "utf-8", null\)\s*\}\s*\},\s*modifier = (Modifier\.fillMaxWidth\(\)\.height\(150\.dp\)\.clip\(RoundedCornerShape\(16\.dp\)\))\s*\)'

replacement1 = r'''AsyncImage(
                                model = "\1",
                                imageLoader = ImageLoader.Builder(context).components {
                                    if (Build.VERSION.SDK_INT >= 28) {
                                        add(ImageDecoderDecoder.Factory())
                                    } else {
                                        add(GifDecoder.Factory())
                                    }
                                }.build(),
                                contentDescription = "Focus GIF",
                                modifier = \2
                            )'''

content = re.sub(pattern1, replacement1, content, flags=re.DOTALL)

pattern2 = r'AndroidView\(\s*factory = \{ ctx ->\s*WebView\(ctx\)\.apply \{\s*settings\.javaScriptEnabled = true\s*setBackgroundColor\(android\.graphics\.Color\.TRANSPARENT\)\s*loadDataWithBaseURL\(null, \"\"\"[^"]*<img src="(https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif)" />[^"]*\"\"\", "text/html", "utf-8", null\)\s*\}\s*\},\s*modifier = (Modifier\.fillMaxWidth\(\)\.height\(200\.dp\)\.clip\(RoundedCornerShape\(16\.dp\)\))\s*\)'

replacement2 = r'''AsyncImage(
                        model = "\1",
                        imageLoader = ImageLoader.Builder(context).components {
                            if (Build.VERSION.SDK_INT >= 28) {
                                add(ImageDecoderDecoder.Factory())
                            } else {
                                add(GifDecoder.Factory())
                            }
                        }.build(),
                        contentDescription = "Celebrate GIF",
                        modifier = \2
                    )'''

content = re.sub(pattern2, replacement2, content, flags=re.DOTALL)

pattern3 = r'AndroidView\(\s*factory = \{ ctx ->\s*WebView\(ctx\)\.apply \{\s*settings\.javaScriptEnabled = true\s*setBackgroundColor\(android\.graphics\.Color\.TRANSPARENT\)\s*loadDataWithBaseURL\(null, \"\"\"[^"]*<img src="(https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif)" />[^"]*\"\"\", "text/html", "utf-8", null\)\s*\}\s*\},\s*modifier = (Modifier\.fillMaxWidth\(\)\.height\(120\.dp\)\.clip\(RoundedCornerShape\(16\.dp\)\))\s*\)'

replacement3 = r'''AsyncImage(
                        model = "\1",
                        imageLoader = ImageLoader.Builder(context).components {
                            if (Build.VERSION.SDK_INT >= 28) {
                                add(ImageDecoderDecoder.Factory())
                            } else {
                                add(GifDecoder.Factory())
                            }
                        }.build(),
                        contentDescription = "Cheer GIF",
                        modifier = \2
                    )'''

content = re.sub(pattern3, replacement3, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "w") as f:
    f.write(content)
