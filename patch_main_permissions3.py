with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if "// Auto-request usage stats if not granted" in line:
        skip = True
        new_lines.append("    val appOps = getSystemService(android.content.Context.APP_OPS_SERVICE) as android.app.AppOpsManager\n")
        new_lines.append("    val mode = appOps.unsafeCheckOpNoThrow(android.app.AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)\n")
        new_lines.append("    val usageAllowed = mode == android.app.AppOpsManager.MODE_ALLOWED\n")
        new_lines.append("    val displayAllowed = android.provider.Settings.canDrawOverlays(this)\n")
        new_lines.append("    if (usageAllowed && displayAllowed) {\n")
        new_lines.append("        try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }\n")
        new_lines.append("    }\n")
    if "override fun onCreate(savedInstanceState: Bundle?)" in line:
        skip = False
    
    if not skip:
        new_lines.append(line)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.writelines(new_lines)
