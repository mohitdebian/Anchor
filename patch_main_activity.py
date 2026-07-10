import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
class MainActivity : ComponentActivity() {
  private var _blockedAppIntent = androidx.compose.runtime.mutableStateOf<String?>(null)

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    _blockedAppIntent.value = intent.getStringExtra("blocked_app_package")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }
    _blockedAppIntent.value = intent.getStringExtra("blocked_app_package")
    enableEdgeToEdge()
    setContent {
      AnchorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val appContainer = (application as AnchorApplication).container
            val blockedApp = _blockedAppIntent.value
            AnchorNavigation(appContainer = appContainer, initialBlockedApp = blockedApp)
        }
      }
    }
  }
}
"""

content = re.sub(r'class MainActivity : ComponentActivity\(\) \{[\s\S]*?\}\s*\}$', replacement.strip(), content)

with open(file_path, "w") as f:
    f.write(content)
