with open("app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "r") as f:
    content = f.read()

import_webview = "import android.webkit.WebView\nimport androidx.compose.ui.viewinterop.AndroidView\n"
if "AndroidView" not in content:
    content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\n" + import_webview)

gif_view = """
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
                            .tenor-gif-embed { width: 100%; max-width: 150px; border-radius: 16px; overflow: hidden; }
                            </style>
                            </head>
                            <body>
                            <div class="tenor-gif-embed" data-postid="17163820" data-share-method="host" data-aspect-ratio="1.23333" data-width="100%"><a href="https://tenor.com/view/welcome-gif-17163820">Welcome GIF</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                            </body>
                            </html>
                            \"\"\", "text/html", "utf-8", null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
"""

content = content.replace("Text(\"Log in or Sign up\", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)", "Text(\"Log in or Sign up\", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)\n" + gif_view)

with open("app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "w") as f:
    f.write(content)
