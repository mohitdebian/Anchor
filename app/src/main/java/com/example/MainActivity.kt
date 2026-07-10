package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import com.example.ui.theme.AnchorTheme
import com.example.navigation.AnchorNavigation

class MainActivity : ComponentActivity() {
  private var _blockedAppIntent = androidx.compose.runtime.mutableStateOf<String?>(null)

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    _blockedAppIntent.value = intent.getStringExtra("blocked_app_package")
  }

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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
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
            AnchorNavigation(appContainer = appContainer, initialBlockedApp = blockedApp, onClearBlockedApp = { _blockedAppIntent.value = null })
        }
      }
    }
  }
}
