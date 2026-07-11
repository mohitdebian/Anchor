package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff

import androidx.compose.material3.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.*

import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.os.Build

import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import android.content.pm.PackageManager
import android.content.Intent
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.widget.ImageView
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.screens.block.AppItem

import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingButton
import com.example.ui.components.BottomNavigationBar
import com.example.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel(factory = com.example.viewmodels.AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDashboardData()
                val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                val userName = prefs.getString("userName", null)
                val userEmail = prefs.getString("userEmail", null)
                val userPhotoUrl = prefs.getString("userPhotoUrl", null)
                viewModel.updateProfile(userName, userEmail, userPhotoUrl)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var showTimerDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(25) }

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
    
    var showGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalMinutes by remember(uiState.dailyGoalMinutes) { mutableStateOf(uiState.dailyGoalMinutes) }
    
    var isScreenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isScreenVisible = true
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    
    val streakPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streakPulseScale"
    )
    
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulseScale"
    )
    
    val card1Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 0, easing = FastOutSlowInEasing),
        label = "card1Alpha"
    )
    val card1OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 0, easing = FastOutSlowInEasing),
        label = "card1OffsetY"
    )
    val card2Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "card2Alpha"
    )
    val card2OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "card2OffsetY"
    )
    val card3Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "card3Alpha"
    )
    val card3OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "card3OffsetY"
    )
    val card4Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "card4Alpha"
    )
    val card4OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "card4OffsetY"
    )
    val card5Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "card5Alpha"
    )
    val card5OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "card5OffsetY"
    )
    
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Daily Goal") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val h = selectedGoalMinutes / 60
                    val m = selectedGoalMinutes % 60
                    val goalText = if (h > 0) "${h}h ${m}m" else "${m}m"
                    Text(goalText, style = MaterialTheme.typography.headlineMedium)
                    Slider(
                        value = selectedGoalMinutes.toFloat(),
                        onValueChange = { selectedGoalMinutes = it.toInt() },
                        valueRange = 30f..480f,
                        steps = 14
                    )
                }
            },
            confirmButton = {
                GlowingButton(
                    onClick = {
                        showGoalDialog = false
                        viewModel.updateDailyGoal(selectedGoalMinutes)
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color(0xFF10B981),
                    contentColor = Color.White
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    if (showTimerDialog) {
        ModalBottomSheet(
            onDismissRequest = { showTimerDialog = false },
            sheetState = sheetState,
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    "DURATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                        .padding(24.dp)
                ) {
                    Text("$selectedMinutes Minutes", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
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
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        items(manualApps) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app.name, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
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


    Scaffold(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background, // Very dark background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Anchor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        if (uiState.userPhotoUrl != null) {
                            coil.compose.AsyncImage(
                                model = uiState.userPhotoUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "dashboard",
                onNavigateToFocus = { /* Stay on dashboard */ },
                onNavigateToPlanner = onNavigateToPlanner,
                onNavigateToBlocks = onNavigateToBlocks,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToAIStats = onNavigateToAIStats
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            com.example.ui.components.BackgroundDoodles()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isTimerActive) {
                    StyledGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = card1Alpha
                                translationY = card1OffsetY.toPx()
                            },
                        borderColor = Color(0xFF059669).copy(alpha = 0.3f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = if (uiState.isTimerPaused) "PAUSED" else "FOCUSING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isTimerPaused) Color.Gray else Color(0xFF059669),
                                letterSpacing = 2.sp
                            )

                            if (!uiState.isTimerPaused) {
                                Spacer(modifier = Modifier.height(16.dp))
                                AndroidView(
                                    factory = { ctx ->
                                        WebView(ctx).apply {
                                            settings.javaScriptEnabled = true
                                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                            loadDataWithBaseURL(null, """
                                            <!DOCTYPE html>
                                            <html>
                                            <head>
                                            <style>
                                            body { margin: 0; padding: 0; background-color: transparent; display: flex; justify-content: center; align-items: center; }
                                            .tenor-gif-embed { width: 100%; max-width: 150px; border-radius: 16px; overflow: hidden; }
                                            </style>
                                            </head>
                                            <body>
                                            <div class="tenor-gif-embed" data-postid="23485600" data-share-method="host" data-aspect-ratio="1" data-width="100%"><a href="https://tenor.com/view/focus-work-hard-concentrate-study-gif-23485600">Focus Work Hard GIF</a></div> <script type="text/javascript" async src="https://tenor.com/embed.js"></script>
                                            </body>
                                            </html>
                                            """, "text/html", "utf-8", null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            val minutes = uiState.remainingTimerSeconds / 60
                            val seconds = uiState.remainingTimerSeconds % 60
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds),
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // App icons row for blocked apps
Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable { onNavigateToBlocks() }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Block, contentDescription = "Blocked Apps", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${uiState.blockedAppsCount} apps blocked",
                                    fontSize = 16.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                if (uiState.isTimerPaused) {
                                    GlowingButton(
                                        onClick = { viewModel.resumeTimer() },
                                        modifier = Modifier.weight(1f),
                                        color = Color(0xFF059669),
                                        contentColor = Color.White
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                                        Spacer(Modifier.width(8.dp))
                                        Text("Resume")
                                    }
                                } else {
                                    GlowingButton(
                                        onClick = { viewModel.pauseTimer() },
                                        modifier = Modifier.weight(1f),
                                        color = Color(0xFF333333),
                                        contentColor = Color.White
                                    ) {
                                        Icon(Icons.Default.Pause, contentDescription = "Pause")
                                        Spacer(Modifier.width(8.dp))
                                        Text("Pause")
                                    }
                                }
                                GlowingButton(
                                    onClick = { viewModel.stopTimer((context.applicationContext as com.example.AnchorApplication).container.ambientSoundManager) },
                                    modifier = Modifier.weight(1f),
                                    color = Color.Red.copy(alpha = 0.7f),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Stop")
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            val ambientSoundManager = (context.applicationContext as com.example.AnchorApplication).container.ambientSoundManager
                            GlowingButton(
                                onClick = { viewModel.toggleAmbientSound(ambientSoundManager) },
                                modifier = Modifier.fillMaxWidth(),
                                color = if (uiState.isAmbientPlaying) Color(0xFF10B981) else Color(0xFF333333),
                                contentColor = Color.White
                            ) {
                                Icon(
                                    if (uiState.isAmbientPlaying) Icons.Default.MusicOff else Icons.Default.MusicNote,
                                    contentDescription = "White Noise"
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(if (uiState.isAmbientPlaying) "Stop White Noise" else "Play White Noise")
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Greetings Area
                val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val greeting = when (currentHour) {
                    in 5..11 -> "Good morning"
                    in 12..16 -> "Good afternoon"
                    in 17..20 -> "Good evening"
                    else -> "Good night"
                }
                Text(
                    text = "$greeting, ${uiState.userName ?: "there"}.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.insightText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.progress >= 1f) {
                    AsyncImage(
                        model = "https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif",
                        imageLoader = ImageLoader.Builder(context).components {
                            if (Build.VERSION.SDK_INT >= 28) {
                                add(ImageDecoderDecoder.Factory())
                            } else {
                                add(GifDecoder.Factory())
                            }
                        }.build(),
                        contentDescription = "Celebrate GIF",
                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    AsyncImage(
                        model = "https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif",
                        imageLoader = ImageLoader.Builder(context).components {
                            if (Build.VERSION.SDK_INT >= 28) {
                                add(ImageDecoderDecoder.Factory())
                            } else {
                                add(GifDecoder.Factory())
                            }
                        }.build(),
                        contentDescription = "Cheer GIF",
                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp))
                    )
                }

                
                Spacer(modifier = Modifier.height(24.dp))
                
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.todaySchedules.isEmpty()) {
                    Text(
                        text = "No schedules for today.",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                } else {
                    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.todaySchedules.forEach { schedule ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF132018).copy(alpha = 0.75f))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = schedule.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = schedule.title, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${schedule.startTime} - ${schedule.endTime}", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                                // Streak Card
                StyledGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = card1Alpha
                            translationY = card1OffsetY.toPx()
                        },
                    borderColor = Color(0xFFFF9F0A).copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9F0A).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFFF9F0A), // Warm fire orange/amber
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(streakPulseScale)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "CURRENT STREAK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = uiState.focusStreak,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Focus Time Card
                StyledGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = card2Alpha
                            translationY = card2OffsetY.toPx()
                        },
                    borderColor = Color(0xFF34D399).copy(alpha = 0.25f)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TODAY'S FOCUS TIME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.5.sp
                            )
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = uiState.focusTimeToday,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            val h = uiState.dailyGoalMinutes / 60
                            val m = uiState.dailyGoalMinutes % 60
                            val goalStr = if (h > 0) "${h}h ${m}m" else "${m}m"
                            Surface(
                                color = Color(0xFF1A3326),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .clickable { showGoalDialog = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "/ $goalStr goal",
                                        fontSize = 12.sp,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Goal",
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Linear Progress Bar
                        val animatedProgress by animateFloatAsState(
                            targetValue = uiState.progress,
                            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                            label = "progress"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1A3326))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF34D399), Color(0xFF10B981))
                                        )
                                    )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!uiState.isTimerActive) {
                    // Start Focus Session Button with inviting breathing animation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = card4Alpha
                                translationY = card4OffsetY.toPx()
                            }
                            .scale(buttonPulseScale),
                        contentAlignment = Alignment.Center
                    ) {
                        GlowingButton(
                            onClick = { showTimerDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF059669), // Vivid blue
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Focus Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StyledGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0xFF10B981).copy(alpha = 0.15f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF132018).copy(alpha = 0.75f)) // Translucent Slate-blue-black glass
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            ),
        content = content
    )
}

@Composable
fun ActivityItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, time: String, iconBgColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = time,
            fontSize = 12.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


