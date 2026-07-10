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
        return
    }
    
    if (!android.provider.Settings.canDrawOverlays(this)) {
        val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, android.net.Uri.parse("package:$packageName"))
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        try { startActivity(intent) } catch (e: Exception) {}
        return
    }

    try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }
  }
"""

content = re.sub(
    r'override fun onResume\(\) \{[\s\S]*?\}\s*\}\s*override fun onCreate',
    replacement.strip() + "\n\n  override fun onCreate",
    content
)

with open(file_path, "w") as f:
    f.write(content)
