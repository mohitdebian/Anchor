with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "r") as f:
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
                    <div class="tenor-gif-embed" data-postid="20967732" data-share-method="host" data-aspect-ratio="1.0" data-width="100%"><a href="https://tenor.com/view/cute-star-twinkle-kawaii-gif-20967732">Cute Star GIF</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                    </body>
                    </html>
                    \"\"\", "text/html", "utf-8", null)
                }
            },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
"""

content = content.replace("modifier = Modifier.padding(horizontal = 24.dp)\n        )", "modifier = Modifier.padding(horizontal = 24.dp)\n        )\n" + gif_view)

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "w") as f:
    f.write(content)
