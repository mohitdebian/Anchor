import re

with open("app/src/main/java/com/example/ui/screens/block/BlockedOverlayScreen.kt", "r") as f:
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

pattern = r'AndroidView\(\s*factory = \{ ctx ->\s*WebView\(ctx\)\.apply \{\s*settings\.javaScriptEnabled = true.*?</script>\\n                        </body>\\n                        </html>\\n                        """, "text/html", "utf-8", null\)\s*\}\s*\},\s*modifier = Modifier\.fillMaxWidth\(\)\.height\(200\.dp\)\s*\)'

replacement = r'''
            val gifs = listOf(
                "https://media.giphy.com/media/11tTNkNy1SdXGg/giphy.gif",
                "https://media.giphy.com/media/xT5LMzIK1AdZJ4cAgE/giphy.gif",
                "https://media.giphy.com/media/3o6wrvdHFbwBrUFenu/giphy.gif"
            )
            val randomGif = remember { gifs.random() }
            
            AsyncImage(
                model = randomGif,
                imageLoader = ImageLoader.Builder(context).components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }.build(),
                contentDescription = "Regret GIF",
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp))
            )
'''

# Wait, let's just do a simpler regex matching AndroidView since there is only one
pattern2 = r'AndroidView\([\s\S]*?Modifier\.fillMaxWidth\(\)\.height\(200\.dp\)\s*\)'

content = re.sub(pattern2, replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/block/BlockedOverlayScreen.kt", "w") as f:
    f.write(content)
