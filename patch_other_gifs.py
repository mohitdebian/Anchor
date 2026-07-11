with open("app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "r") as f:
    content = f.read()

gif_old = """                            <!DOCTYPE html>
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
                            </html>"""

gif_new = """                            <!DOCTYPE html>
                            <html>
                            <head>
                            <style>
                            body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                            img { width: 100%; max-width: 150px; border-radius: 16px; }
                            </style>
                            </head>
                            <body>
                            <img src="https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif" />
                            </body>
                            </html>"""

content = content.replace(gif_old, gif_new)

with open("app/src/main/java/com/example/ui/screens/onboarding/LoginScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "r") as f:
    content2 = f.read()

gif2_old = """                    <!DOCTYPE html>
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
                    </html>"""

gif2_new = """                    <!DOCTYPE html>
                    <html>
                    <head>
                    <style>
                    body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                    img { width: 100%; max-width: 150px; border-radius: 16px; }
                    </style>
                    </head>
                    <body>
                    <img src="https://media.giphy.com/media/ASd0Ukj0y3qMM/giphy.gif" />
                    </body>
                    </html>"""

content2 = content2.replace(gif2_old, gif2_new)

with open("app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt", "w") as f:
    f.write(content2)
