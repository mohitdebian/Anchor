import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Make sure imports are present
imports_to_add = """
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
"""

if "import androidx.compose.material3.ModalBottomSheet" not in content:
    content = content.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\n" + imports_to_add.strip())

# We will search for 'if (showTimerDialog) {' and replace it with the new code
old_dialog = r'if \(showTimerDialog\) \{[\s\S]*?dismissButton = \{\s*TextButton\(onClick = \{ showTimerDialog = false \}\) \{\s*Text\("Cancel"\)\s*\}\s*\}\s*\)\s*\}'

new_dialog = """
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    if (showTimerDialog) {
        ModalBottomSheet(
            onDismissRequest = { showTimerDialog = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1C1C1E),
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "Configure Session",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    "DURATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2C2C2E))
                        .padding(24.dp)
                ) {
                    Text("$selectedMinutes Minutes", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = { selectedMinutes = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 22
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "APPS TO BLOCK",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (isLoadingApps) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF10B981))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2C2C2E))
                    ) {
                        items(manualApps) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app.name, color = Color.White, fontSize = 16.sp)
                                Switch(
                                    checked = app.isBlocked,
                                    onCheckedChange = { checked ->
                                        sharedPreferences.edit().putBoolean(app.packageName, checked).apply()
                                        val index = manualApps.indexOfFirst { it.packageName == app.packageName }
                                        if (index != -1) {
                                            val newList = manualApps.toMutableList()
                                            newList[index] = app.copy(isBlocked = checked)
                                            manualApps = newList
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF10B981))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                GlowingButton(
                    onClick = {
                        showTimerDialog = false
                        viewModel.startTimer(selectedMinutes)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    color = Color(0xFF10B981),
                    contentColor = Color.White
                ) {
                    Text("Start Focus Session", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
"""

content = re.sub(old_dialog, new_dialog, content)

with open(file_path, "w") as f:
    f.write(content)
