with open("app/src/main/java/com/example/AppContainer.kt", "r") as f:
    content = f.read()

content = content.replace("override val themeManager: ThemeManager\n    val ambientSoundManager: AmbientSoundManager by lazy { ThemeManager(context) }", "override val themeManager: ThemeManager by lazy { ThemeManager(context) }\n    override val ambientSoundManager: AmbientSoundManager by lazy { AmbientSoundManager() }")

with open("app/src/main/java/com/example/AppContainer.kt", "w") as f:
    f.write(content)
