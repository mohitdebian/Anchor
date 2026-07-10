import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
  override fun onResume() {
    super.onResume()
    
    // Auto-request usage stats if not granted
    val appOps = getSystemService(android.content.Context.APP_OPS_SERVICE) as android.app.AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(android.app.AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
    if (mode != android.app.AppOpsManager.MODE_ALLOWED) {
        val intent = android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        try { startActivity(intent) } catch (e: Exception) {}
    } else {
        // Only start service if usage stats is granted
        try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
"""

content = content.replace("  override fun onCreate(savedInstanceState: Bundle?) {", replacement.strip())

# Remove the old startService from onCreate
content = content.replace("try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }", "")

with open(file_path, "w") as f:
    f.write(content)
