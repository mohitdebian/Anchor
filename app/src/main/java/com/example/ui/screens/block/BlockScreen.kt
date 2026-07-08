package com.example.ui.screens.block

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppItem(val packageName: String, val name: String, val isBlocked: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE) }
    var apps by remember { mutableStateOf(emptyList<AppItem>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val appList = mutableListOf<AppItem>()
            for (appInfo in packages) {
                // Only show user-installed apps, skip system apps
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    val label = pm.getApplicationLabel(appInfo).toString()
                    val packageName = appInfo.packageName
                    val isBlocked = sharedPreferences.getBoolean(packageName, false)
                    appList.add(AppItem(packageName, label, isBlocked))
                }
            }
            appList.sortBy { it.name }
            apps = appList
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { Text("Blocked Apps", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Manage your focus by selecting which apps to block during active sessions.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1A56FF))
                }
            } else if (apps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No installable apps found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps) { app ->
                        AppBlockItem(
                            app = app,
                            onToggle = { isBlocked ->
                                sharedPreferences.edit().putBoolean(app.packageName, isBlocked).apply()
                                apps = apps.map { if (it.packageName == app.packageName) it.copy(isBlocked = isBlocked) else it }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppBlockItem(app: AppItem, onToggle: (Boolean) -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    Icons.Default.Block,
                    contentDescription = null,
                    tint = if (app.isBlocked) Color(0xFFEF4444) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = app.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
            
            Switch(
                checked = app.isBlocked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF1A56FF),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF333333)
                )
            )
        }
    }
}
