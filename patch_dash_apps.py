import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Add imports if missing
imports_to_add = """
import android.content.pm.PackageManager
import android.content.Intent
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.widget.ImageView
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.screens.block.AppItem
"""
content = content.replace('import androidx.compose.ui.unit.sp', imports_to_add + '\nimport androidx.compose.ui.unit.sp')

# Add app loading logic
app_logic = """
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("blocked_apps", android.content.Context.MODE_PRIVATE) }
    var manualApps by remember { mutableStateOf(emptyList<AppItem>()) }
    var isLoadingApps by remember { mutableStateOf(true) }

    LaunchedEffect(showTimerDialog) {
        if (showTimerDialog) {
            isLoadingApps = true
            withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
                val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                val appList = mutableListOf<AppItem>()
                for (resolveInfo in resolveInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    if (packageName != context.packageName) {
                        val label = resolveInfo.loadLabel(pm).toString()
                        val isBlocked = sharedPreferences.getBoolean(packageName, false)
                        appList.add(AppItem(packageName, label, isBlocked))
                    }
                }
                manualApps = appList.distinctBy { it.packageName }.sortedBy { it.name }
                isLoadingApps = false
            }
        }
    }
"""
content = re.sub(r'var showTimerDialog by remember \{ mutableStateOf\(false\) \}\n\s*var selectedMinutes by remember \{ mutableStateOf\(25\) \}', 
                 'var showTimerDialog by remember { mutableStateOf(false) }\n    var selectedMinutes by remember { mutableStateOf(25) }\n' + app_logic, 
                 content)

# Replace the AlertDialog with a full-screen Dialog
dialog_logic = """
    if (showTimerDialog) {
        Dialog(
            onDismissRequest = { showTimerDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF121212)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Text("Start Focus Session", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Duration: $selectedMinutes Minutes", style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = { selectedMinutes = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 22,
                        colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Select Apps to Block", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (isLoadingApps) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF10B981))
                        }
                    } else {
                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(manualApps, key = { it.packageName }) { app ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF1E1E1E)).padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AndroidView(
                                        factory = { ctx ->
                                            ImageView(ctx).apply {
                                                try {
                                                    setImageDrawable(ctx.packageManager.getApplicationIcon(app.packageName))
                                                } catch (e: Exception) {}
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(app.name, color = Color.White, modifier = Modifier.weight(1f), maxLines = 1)
                                    Switch(
                                        checked = app.isBlocked,
                                        onCheckedChange = { isBlocked ->
                                            sharedPreferences.edit().putBoolean(app.packageName, isBlocked).apply()
                                            manualApps = manualApps.map { if (it.packageName == app.packageName) it.copy(isBlocked = isBlocked) else it }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimerDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                showTimerDialog = false
                                viewModel.startTimer(selectedMinutes)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Start Timer")
                        }
                    }
                }
            }
        }
    }
"""

content = re.sub(r'if \(showTimerDialog\) \{[\s\S]*?\}\s*\}\s*Scaffold\(', dialog_logic.strip() + "\n\n    Scaffold(", content)

with open(file_path, "w") as f:
    f.write(content)
