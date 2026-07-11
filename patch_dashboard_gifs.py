with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "r") as f:
    content = f.read()

content = content.replace('Text("Stop")\n                                }\n                            Spacer(modifier = Modifier.height(16.dp))', 'Text("Stop")\n                                }\n                            }\n                            Spacer(modifier = Modifier.height(16.dp))')

content = content.replace('Text(if (uiState.isAmbientPlaying) "Stop White Noise" else "Play White Noise")\n                            }\n                            }\n                        }\n                    }\n                    Spacer(modifier = Modifier.height(24.dp))', 'Text(if (uiState.isAmbientPlaying) "Stop White Noise" else "Play White Noise")\n                            }\n                        }\n                    }\n                    Spacer(modifier = Modifier.height(24.dp))')


gif1_old = """                                <!DOCTYPE html>
                                <html>
                                <head>
                                <style>
                                body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                                .tenor-gif-embed { width: 100%; max-width: 150px; border-radius: 16px; overflow: hidden; }
                                </style>
                                </head>
                                <body>
                                <div class="tenor-gif-embed" data-postid="23485600" data-share-method="host" data-aspect-ratio="1" data-width="100%"><a href="https://tenor.com/view/focus-work-hard-concentrate-study-gif-23485600">Focus Work Hard GIF</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                                </body>
                                </html>"""

gif1_new = """                                <!DOCTYPE html>
                                <html>
                                <head>
                                <style>
                                body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                                img { width: 100%; max-width: 150px; border-radius: 16px; }
                                </style>
                                </head>
                                <body>
                                <img src="https://media.giphy.com/media/13HgwGsXF0aiGY/giphy.gif" />
                                </body>
                                </html>"""

content = content.replace(gif1_old, gif1_new)

gif2_old = """                                <!DOCTYPE html>
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
                                </html>"""

gif2_new = """                                <!DOCTYPE html>
                                <html>
                                <head>
                                <style>
                                body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                                img { width: 100%; max-width: 250px; border-radius: 16px; }
                                </style>
                                </head>
                                <body>
                                <img src="https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif" />
                                </body>
                                </html>"""

content = content.replace(gif2_old, gif2_new)

gif3_old = """                                <!DOCTYPE html>
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
                                </html>"""

gif3_new = """                                <!DOCTYPE html>
                                <html>
                                <head>
                                <style>
                                body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                                img { width: 100%; max-width: 200px; border-radius: 16px; }
                                </style>
                                </head>
                                <body>
                                <img src="https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif" />
                                </body>
                                </html>"""

content = content.replace(gif3_old, gif3_new)

with open("app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt", "w") as f:
    f.write(content)
