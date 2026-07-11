with open("app/src/main/java/com/example/AppContainer.kt", "r") as f:
    content = f.read()

ambient_import = "import com.example.services.AmbientSoundManager\n"
if "import com.example.services.AmbientSoundManager" not in content:
    content = content.replace("import com.example.data.database.AppDatabase", ambient_import + "import com.example.data.database.AppDatabase")

if "val ambientSoundManager" not in content:
    content = content.replace("val themeManager: ThemeManager", "val themeManager: ThemeManager\n    val ambientSoundManager: AmbientSoundManager")
    content = content.replace("override val themeManager: ThemeManager by lazy { ThemeManager(context) }", "override val themeManager: ThemeManager by lazy { ThemeManager(context) }\n    override val ambientSoundManager: AmbientSoundManager by lazy { AmbientSoundManager() }")

with open("app/src/main/java/com/example/AppContainer.kt", "w") as f:
    f.write(content)
