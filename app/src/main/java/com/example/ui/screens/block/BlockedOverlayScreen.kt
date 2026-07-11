package com.example.ui.screens.block

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*

import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.os.Build

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodels.AppViewModelProvider

@Composable
fun BlockedOverlayScreen(
    packageName: String, 
    onGoHome: () -> Unit,
    viewModel: BlockedOverlayViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    var appName by remember { mutableStateOf(packageName) }
    
    val roastMessage by viewModel.roastMessage.collectAsState()

    LaunchedEffect(packageName) {
        val pm = context.packageManager
        try {
            val appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            appName = pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {}
        
        viewModel.fetchRoast(appName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🚫", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "$appName is Blocked",
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Text(
                    text = roastMessage ?: "Analyzing your distraction...",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val gifs = listOf(
                "https://media.giphy.com/media/11tTNkNy1SdXGg/giphy.gif",
                "https://media.giphy.com/media/xT5LMzIK1AdZJ4cAgE/giphy.gif",
                "https://media.giphy.com/media/3o6wrvdHFbwBrUFenu/giphy.gif"
            )
            val randomGif = remember { gifs.random() }
            
            AsyncImage(
                model = randomGif,
                imageLoader = ImageLoader.Builder(context).components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }.build(),
                contentDescription = "Regret GIF",
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp))
            )


            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onGoHome,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Go Back to Work", color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { 
                    val startMain = Intent(Intent.ACTION_MAIN)
                    startMain.addCategory(Intent.CATEGORY_HOME)
                    startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(startMain)
                }
            ) {
                Text("Go to Home Screen", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
