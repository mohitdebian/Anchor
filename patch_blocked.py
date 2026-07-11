with open("app/src/main/java/com/example/ui/screens/block/BlockedOverlayScreen.kt", "r") as f:
    content = f.read()

import_webview = "import android.webkit.WebView\nimport androidx.compose.ui.viewinterop.AndroidView\n"
if "AndroidView" not in content:
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\n" + import_webview)

webview_code = """
            Spacer(modifier = Modifier.height(24.dp))
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        loadDataWithBaseURL(null, \"\"\"
                        <!DOCTYPE html>
                        <html>
                        <head>
                        <style>
                        body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                        .tenor-gif-embed { width: 100%; max-width: 300px; }
                        </style>
                        </head>
                        <body>
                        <div class="tenor-gif-embed" data-postid="26223355" data-share-method="host" data-aspect-ratio="1.78771" data-width="100%"><a href="https://tenor.com/view/anime-angry-mad-gif-26223355">Anime Angry GIF</a>from <a href="https://tenor.com/search/anime-gifs">Anime GIFs</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                        </body>
                        </html>
                        \"\"\", "text/html", "utf-8", null)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
"""

content = content.replace("Spacer(modifier = Modifier.height(48.dp))", webview_code + "\n            Spacer(modifier = Modifier.height(24.dp))")
content = content.replace("Color(0xFF0F0F0F)", "androidx.compose.material3.MaterialTheme.colorScheme.background")

with open("app/src/main/java/com/example/ui/screens/block/BlockedOverlayScreen.kt", "w") as f:
    f.write(content)
