with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

import_collect = "import androidx.compose.runtime.collectAsState\n"
if "collectAsState" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\n" + import_collect)

content = content.replace("""    setContent {
      AnchorTheme {""", """    setContent {
      val appContainer = (application as AnchorApplication).container
      val isDarkTheme by appContainer.themeManager.isDarkTheme.collectAsState()
      AnchorTheme(darkTheme = isDarkTheme) {""")

content = content.replace("""        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val appContainer = (application as AnchorApplication).container
            val blockedApp = _blockedAppIntent.value""", """        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val blockedApp = _blockedAppIntent.value""")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
