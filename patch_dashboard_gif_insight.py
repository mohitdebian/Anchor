with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "r") as f:
    content = f.read()

insight_gif = """
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.progress >= 1f) {
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
                                .tenor-gif-embed { width: 100%; max-width: 250px; border-radius: 16px; overflow: hidden; }
                                </style>
                                </head>
                                <body>
                                <div class="tenor-gif-embed" data-postid="17950346" data-share-method="host" data-aspect-ratio="1.33333" data-width="100%"><a href="https://tenor.com/view/celebrate-party-yay-happy-dancing-gif-17950346">Celebrate GIF</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                                </body>
                                </html>
                                \"\"\", "text/html", "utf-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp))
                    )
                } else {
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
                                .tenor-gif-embed { width: 100%; max-width: 200px; border-radius: 16px; overflow: hidden; }
                                </style>
                                </head>
                                <body>
                                <div class="tenor-gif-embed" data-postid="15383569" data-share-method="host" data-aspect-ratio="1.23077" data-width="100%"><a href="https://tenor.com/view/lets-go-come-on-you-can-do-it-cheering-minions-gif-15383569">Lets Go Come On GIF</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                                </body>
                                </html>
                                \"\"\", "text/html", "utf-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp))
                    )
                }
"""

content = content.replace('color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant\n                )', 'color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant\n                )\n' + insight_gif)

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "w") as f:
    f.write(content)
