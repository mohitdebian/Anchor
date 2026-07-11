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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

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
    
    val appOps = getSystemService(android.content.Context.APP_OPS_SERVICE) as android.app.AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(android.app.AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
    val usageAllowed = mode == android.app.AppOpsManager.MODE_ALLOWED
    val displayAllowed = android.provider.Settings.canDrawOverlays(this)
    if (usageAllowed && displayAllowed) {
        try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    _blockedAppIntent.value = intent.getStringExtra("blocked_app_package")
    enableEdgeToEdge()
    setContent {
      val appContainer = (application as AnchorApplication).container
      val isDarkTheme by appContainer.themeManager.isDarkTheme.collectAsState()
      AnchorTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val blockedApp = _blockedAppIntent.value
            AnchorNavigation(appContainer = appContainer, initialBlockedApp = blockedApp, onClearBlockedApp = { _blockedAppIntent.value = null })
        }
      }
    }
  }
}
