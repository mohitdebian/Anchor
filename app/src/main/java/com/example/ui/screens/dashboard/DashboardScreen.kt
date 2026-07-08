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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = com.example.viewmodels.AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    var showGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalMinutes by remember(uiState.dailyGoalMinutes) { mutableStateOf(uiState.dailyGoalMinutes) }

    // Entrance staggered animations triggers
    var isScreenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isScreenVisible = true
    }

    // Infinite transitions for pulsing animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    
    // Streak flame pulsing
    val streakPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streakPulseScale"
    )

    // Main Focus Button breathing pulse (idle state)
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulseScale"
    )

    // Staggered card properties
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
                        valueRange = 30f..480f, // 30m to 8h
                        steps = 14 // 30m steps
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

    if (showTimerDialog) {
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            title = { Text("Set Focus Duration") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$selectedMinutes Minutes", style = MaterialTheme.typography.headlineMedium)
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = { selectedMinutes = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 22
                    )
                }
            },
            confirmButton = {
                GlowingButton(
                    onClick = {
                        showTimerDialog = false
                        viewModel.startTimer(selectedMinutes)
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color(0xFF10B981),
                    contentColor = Color.White
                ) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF121212), // Very dark background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Anchor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.LightGray)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
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
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.LightGray)
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
                            Spacer(modifier = Modifier.height(16.dp))
                            val minutes = uiState.remainingTimerSeconds / 60
                            val seconds = uiState.remainingTimerSeconds % 60
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds),
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // App icons row for blocked apps
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE1306C)) // Instagram-like pink
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4285F4)) // Chrome/Google-like blue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFBBC05)) // Myntra/Shopping yellow
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "6 apps blocked",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                                    onClick = { viewModel.stopTimer() },
                                    modifier = Modifier.weight(1f),
                                    color = Color.Red.copy(alpha = 0.7f),
                                    contentColor = Color.White
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Stop")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Greetings Area
                Text(
                    text = "Good morning, ${uiState.userName ?: "there"}.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.insightText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray
                )
                
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
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = uiState.focusStreak,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                                color = Color.Gray,
                                letterSpacing = 1.5.sp
                            )
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = uiState.focusTimeToday,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Goal",
                                        tint = Color.LightGray,
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
                
                // Screen Time and Blocked Alerts Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = card3Alpha
                            translationY = card3OffsetY.toPx()
                        },
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Screen Time Card
                    StyledGlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (uiState.screenTime == "Needs Permission") viewModel.requestUsagePermission(context) },
                        borderColor = Color(0xFF00C7FF).copy(alpha = 0.15f)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SCREEN TIME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    letterSpacing = 1.sp
                                )
                                Icon(
                                    Icons.Default.PhoneIphone,
                                    contentDescription = "Screen Time",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.screenTime,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.TrendingDown,
                                    contentDescription = "Trending Down",
                                    tint = Color(0xFF30D158),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "-12% vs yesterday",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                    
                    // Blocked Alerts Card
                    StyledGlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = Color(0xFFFF453A).copy(alpha = 0.15f)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "BLOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    letterSpacing = 1.sp
                                )
                                Icon(
                                    Icons.Default.Block,
                                    contentDescription = "Blocked",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "${uiState.blockedAlerts} Alerts",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = Color(0xFF30D158),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "High Security",
                                    fontSize = 11.sp,
                                    color = Color.LightGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
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
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Focus Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Recent Activity Header
                Text(
                    text = "RECENT ACTIVITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = card5Alpha
                        translationY = card5OffsetY.toPx()
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Recent Activity List Container
                StyledGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = card5Alpha
                            translationY = card5OffsetY.toPx()
                        },
                    borderColor = Color(0xFF333333).copy(alpha = 0.2f)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (uiState.recentSessions.isEmpty()) {
                            Text(
                                text = "No recent sessions.",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            uiState.recentSessions.forEachIndexed { index, session ->
                                val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                                val timeStr = formatter.format(java.util.Date(session.timestamp))
                                ActivityItem(
                                    icon = Icons.Default.Anchor,
                                    title = "Deep Work Session",
                                    subtitle = "Completed • +${session.durationMinutes}m",
                                    time = timeStr,
                                    iconBgColor = Color(0xFF163321)
                                )
                                if (index < uiState.recentSessions.size - 1) {
                                    HorizontalDivider(color = Color(0xFF333333).copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 20.dp))
                                }
                            }
                        }
                    }
                }
                
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
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
        
        Text(
            text = time,
            fontSize = 12.sp,
            color = Color.LightGray
        )
    }
}


