package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class MascotExpression {
    HAPPY, STUDYING, SAD, DISTRACTED, CURIOUS
}

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    
    // User selections
    var selectedDistraction by remember { mutableStateOf<String?>(null) }
    var selectedFrequency by remember { mutableStateOf<String?>(null) }
    var selectedStudyTime by remember { mutableStateOf(3) } // 3 corresponds to Night by default
    var selectedPreparingExams by remember { mutableStateOf<Boolean?>(null) }
    
    // Permission states
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    var usageAllowed by remember { mutableStateOf(false) }
    var backgroundAllowed by remember { mutableStateOf(false) }
    var displayAllowed by remember { mutableStateOf(false) }
    
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val appOps = context.getSystemService(android.content.Context.APP_OPS_SERVICE) as android.app.AppOpsManager
                val mode = appOps.unsafeCheckOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
                usageAllowed = mode == android.app.AppOpsManager.MODE_ALLOWED

                val powerManager = context.getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager
                backgroundAllowed = powerManager.isIgnoringBatteryOptimizations(context.packageName)

                displayAllowed = android.provider.Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    val totalSteps = 8
    
    Scaffold(
        containerColor = Color(0xFF121212) // Dashboard background color
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Draw background doodles
            com.example.ui.components.BackgroundDoodles()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Bar (visible for Step 1 to 8)
                if (step in 1..8) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom premium thin progress bar
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E261E))
                        ) {
                            val progressFactor = step.toFloat() / totalSteps.toFloat()
                            val animatedProgress by animateFloatAsState(
                                targetValue = progressFactor,
                                animationSpec = tween(500, easing = FastOutSlowInEasing),
                                label = "Progress"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)) // Accent green
                            )
                        }
                    }
                }
                
                // Content area with crossfade transitions
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(
                        targetState = step,
                        animationSpec = tween(400),
                        label = "StepTransition"
                    ) { currentStep ->
                        when (currentStep) {
                            0 -> WelcomeStep(onNext = { step = 1 })
                            1 -> DistractionStep(
                                selected = selectedDistraction,
                                onSelect = { selectedDistraction = it },
                                onNext = { step = 2 }
                            )
                            2 -> FrequencyStep(
                                selected = selectedFrequency,
                                onSelect = { selectedFrequency = it },
                                onNext = { step = 3 }
                            )
                            3 -> StudyTimeStep(
                                selected = selectedStudyTime,
                                onSelect = { selectedStudyTime = it },
                                onNext = { step = 4 }
                            )
                            4 -> StudySetupPreviewStep(
                                selectedStudyTime = selectedStudyTime,
                                onNext = { step = 5 }
                            )
                            5 -> ExamsStep(
                                selected = selectedPreparingExams,
                                onSelect = { selectedPreparingExams = it },
                                onNext = { step = 6 }
                            )
                            6 -> BeforeAfterStep(
                                onNext = { step = 7 }
                            )
                            7 -> ValuePropStep(
                                onNext = { step = 8 }
                            )
                            8 -> PermissionsStep(
                                usageAllowed = usageAllowed,
                                backgroundAllowed = backgroundAllowed,
                                displayAllowed = displayAllowed,
                                onToggleUsage = { },
                                onToggleBackground = { },
                                onToggleDisplay = { },
                                onFinish = onFinish
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        
        // Beautiful floating mascot & logo badge
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(180.dp)) {
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    radius = 70.dp.toPx()
                )
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.05f),
                    radius = 90.dp.toPx(),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                    )
                )
            }
            CuteMascot(
                modifier = Modifier.size(110.dp),
                expression = MascotExpression.HAPPY
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Anchor",
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Reclaim your focus. Build stable, calm study habits with smart scheduling.",
            fontSize = 16.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.weight(0.3f))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("get_started_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun DistractionStep(
    selected: String?,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf(
        "🎬  Scrolling reels & shorts",
        "🔔  Distractions from notifications",
        "📱  Texting on my phone",
        "🎮  Games"
    )
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        MascotBubble(
            text = "What's your biggest distraction when you're trying to focus?",
            expression = MascotExpression.CURIOUS
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        options.forEach { option ->
            val isSelected = selected == option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .testTag("distraction_${option.replace(" ", "_")}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF111611)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isSelected) Color(0xFF10B981) else Color(0xFF232D23)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.LightGray,
                        fontFamily = FontFamily.SansSerif
                    )
                    
                    // Radio button outline/fill
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) Color(0xFF10B981) else Color.Gray, CircleShape)
                            .background(if (isSelected) Color(0xFF10B981) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("distraction_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFF1E261E),
                disabledContentColor = Color.Gray
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun FrequencyStep(
    selected: String?,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf(
        "🥳  I easily keep up with my schedule",
        "😐  Sometimes",
        "🥲  Yes, and I want to change it!"
    )
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        MascotBubble(
            text = "Do you often get distracted before you start focusing?",
            expression = MascotExpression.SAD
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        options.forEach { option ->
            val isSelected = selected == option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .testTag("frequency_${option.replace(" ", "_")}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF111611)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isSelected) Color(0xFF10B981) else Color(0xFF232D23)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.LightGray,
                        fontFamily = FontFamily.SansSerif
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) Color(0xFF10B981) else Color.Gray, CircleShape)
                            .background(if (isSelected) Color(0xFF10B981) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("frequency_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFF1E261E),
                disabledContentColor = Color.Gray
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun StudyTimeStep(
    selected: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit
) {
    val times = listOf(
        "☀️  Morning" to "6AM - 8AM",
        "☀️  Afternoon" to "2PM - 4PM",
        "⛅  Evening" to "6PM - 8PM",
        "🌙  Night" to "8PM - 10PM"
    )
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        MascotBubble(
            text = "When do you study?",
            expression = MascotExpression.STUDYING
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        times.forEachIndexed { index, pair ->
            val isSelected = selected == index
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(index) }
                    .testTag("study_time_$index"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF111611)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isSelected) Color(0xFF10B981) else Color(0xFF232D23)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = pair.first,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pair.second,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) Color(0xFF10B981) else Color.Gray, CircleShape)
                            .background(if (isSelected) Color(0xFF10B981) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Reminder note
        Text(
            text = "You can edit the time later",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            fontFamily = FontFamily.SansSerif
        )
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("study_time_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun StudySetupPreviewStep(
    selectedStudyTime: Int,
    onNext: () -> Unit
) {
    val textAndIcons = when (selectedStudyTime) {
        0 -> Triple("Morning Study\nset up!", "6AM - 8AM", "☀️")
        1 -> Triple("Afternoon Study\nset up!", "2PM - 4PM", "☀️")
        2 -> Triple("Evening Study\nset up!", "6PM - 8PM", "⛅")
        else -> Triple("Night Study\nset up!", "8PM - 10PM", "🌙")
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Custom elegant phone container representing Night Study Setup
        PhoneMockup(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF0F2613), Color(0xFF050805)),
                            center = Offset(500f, 600f)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big glowing icon (Sun/Moon)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color(0xFFFACC15).copy(alpha = 0.15f),
                                radius = 60.dp.toPx()
                            )
                        }
                        Text(
                            text = textAndIcons.third,
                            fontSize = 72.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    Text(
                        text = textAndIcons.first,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 36.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = textAndIcons.second,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "You can edit the time later",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF111611))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            fontFamily = FontFamily.SansSerif
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("preview_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun ExamsStep(
    selected: Boolean?,
    onSelect: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        MascotBubble(
            text = "Are you preparing for any exams?",
            expression = MascotExpression.CURIOUS
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        listOf("Yes" to true, "No" to false).forEach { (label, value) ->
            val isSelected = selected == value
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(value) }
                    .testTag("exams_${label.lowercase()}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF111611)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isSelected) Color(0xFF10B981) else Color(0xFF232D23)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) Color(0xFF10B981) else Color.Gray, CircleShape)
                            .background(if (isSelected) Color(0xFF10B981) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("exams_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFF1E261E),
                disabledContentColor = Color.Gray
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun BeforeAfterStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Within a week, you will see these changes",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 34.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Before Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1A04)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF5E3C0F))
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Before Anchor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFB057),
                                fontFamily = FontFamily.SansSerif
                            )
                            
                            // F- Badge
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53E3E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "F-",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        BulletItem("Phone decides your study hours", Color(0xFFFFD1A9))
                        BulletItem("Endless scrolling", Color(0xFFFFD1A9))
                        BulletItem("Broken focus", Color(0xFFFFD1A9))
                    }
                    
                    // Sad mascot with phone at the bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CuteMascot(
                            modifier = Modifier.size(80.dp),
                            expression = MascotExpression.DISTRACTED
                        )
                    }
                }
            }
            
            // After Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF032B0F)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF0E692D))
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "After Anchor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4ADE80),
                                fontFamily = FontFamily.SansSerif
                            )
                            
                            // A+ Badge
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "A+",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        BulletItem("You control your study time", Color(0xFFA7F3D0))
                        BulletItem("Mindful checking", Color(0xFFA7F3D0))
                        BulletItem("Stable, calm focus", Color(0xFFA7F3D0))
                    }
                    
                    // Happy reading mascot at the bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CuteMascot(
                            modifier = Modifier.size(80.dp),
                            expression = MascotExpression.STUDYING
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("before_after_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun BulletItem(text: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
    }
}


@Composable
fun ValuePropStep(onNext: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_transition")
    
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "robot_float"
    )
    
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )
    
    val visorScanAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "visor_scan"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Schedule focus sessions to beat procrastination and stay consistent",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 32.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Aura
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .scale(pulseAnim)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF10B981).copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            
            // Robot Character Container
            Box(
                modifier = Modifier
                    .offset(y = floatAnim.dp)
                    .size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                // Head
                Box(
                    modifier = Modifier
                        .offset(y = (-35).dp)
                        .size(80.dp, 70.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Visor Track
                    Box(
                        modifier = Modifier
                            .size(50.dp, 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF121212)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanning Eye
                        Box(
                            modifier = Modifier
                                .offset(x = visorScanAnim.dp)
                                .size(16.dp, 8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                    
                    // Antenna
                    Box(
                        modifier = Modifier
                            .offset(y = (-40).dp)
                            .size(6.dp, 15.dp)
                            .background(Color(0xFF393E46), RoundedCornerShape(3.dp))
                    )
                    Box(
                        modifier = Modifier
                            .offset(y = (-48).dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (pulseAnim > 1.0f) Color(0xFF10B981) else Color(0xFFE1306C))
                    )
                }
                
                // Body
                Box(
                    modifier = Modifier
                        .offset(y = 30.dp)
                        .size(60.dp, 50.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 12.dp, topEnd = 12.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 12.dp, topEnd = 12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Chest core
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .scale(pulseAnim)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                }
                
                // Left Arm
                Box(
                    modifier = Modifier
                        .offset(x = (-45).dp, y = 25.dp + (floatAnim * 0.5f).dp)
                        .size(16.dp, 45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(8.dp))
                )
                
                // Right Arm
                Box(
                    modifier = Modifier
                        .offset(x = 45.dp, y = 25.dp + (floatAnim * 0.5f).dp)
                        .size(16.dp, 45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(8.dp))
                )
            }
            
            // Meditating rings
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = Color(0xFF10B981),
                    radius = size.width / 2 * pulseAnim,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f)))
                )
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.5f),
                    radius = (size.width / 2 - 20.dp.toPx()) * (2f - pulseAnim),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text("Schedule now!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
fun PermissionsStep(
    usageAllowed: Boolean,
    backgroundAllowed: Boolean,
    displayAllowed: Boolean,
    onToggleUsage: () -> Unit,
    onToggleBackground: () -> Unit,
    onToggleDisplay: () -> Unit,
    onFinish: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        MascotBubble(
            text = buildAnnotatedString {
                append("Almost there, allow these permissions to ")
                withStyle(style = SpanStyle(color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)) {
                    append("focus alongside 4,734 people")
                }
                append(" now!")
            },
            expression = MascotExpression.HAPPY
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Checklist items
        PermissionItem(
            title = "Usage permission",
            description = "This allows us to track your app usage.",
            allowed = usageAllowed,
            onToggle = {
                val intent = android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                onToggleUsage()
            },
            tag = "permission_usage"
        )
        
        PermissionItem(
            title = "Background permission",
            description = "Allows background locks to remain strictly intact.",
            allowed = backgroundAllowed,
            onToggle = {
                val intent = android.content.Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                onToggleBackground()
            },
            tag = "permission_background"
        )
        
        PermissionItem(
            title = "Display over other apps",
            description = "Required to show fullscreen lock overlays correctly.",
            allowed = displayAllowed,
            onToggle = {
                val intent = android.content.Intent(
                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:${context.packageName}")
                )
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                onToggleDisplay()
            },
            tag = "permission_display"
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom helpers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF1F3D24), RoundedCornerShape(20.dp))
                .background(Color(0xFF0F1E12))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1D3E25))
                        .padding(2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Why should I give this permission?",
                    color = Color(0xFF4ADE80),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp)
            )
        }
        
        Text(
            text = "Trusted by 2M+ students ❤️",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium
        )
        
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("permissions_continue"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    description: String,
    allowed: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0E120E))
            .border(1.dp, Color(0xFF1A221A), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (allowed) Color.White else Color.Gray,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Button(
            onClick = onToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (allowed) Color(0xFF10B981) else Color.White,
                contentColor = if (allowed) Color.White else Color.Black
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier
                .height(36.dp)
                .testTag("${tag}_button")
        ) {
            Text(
                text = if (allowed) "Allowed" else "Allow",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MascotBubble(
    text: String,
    expression: MascotExpression = MascotExpression.HAPPY
) {
    MascotBubble(text = buildAnnotatedString { append(text) }, expression = expression)
}

@Composable
fun MascotBubble(
    text: androidx.compose.ui.text.AnnotatedString,
    expression: MascotExpression = MascotExpression.HAPPY
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        CuteMascot(
            modifier = Modifier.size(64.dp),
            expression = expression
        )
        Spacer(modifier = Modifier.width(12.dp))
        
        // Chat bubble with small pointer
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF151A15))
                .border(1.dp, Color(0xFF222B22), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun CuteMascot(
    modifier: Modifier = Modifier,
    expression: MascotExpression = MascotExpression.HAPPY
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Draw head (cute rounded blob shape)
        val headPath = Path().apply {
            moveTo(w * 0.15f, h * 0.45f)
            // Top head curve
            cubicTo(w * 0.15f, h * 0.18f, w * 0.85f, h * 0.18f, w * 0.85f, h * 0.45f)
            // Right cheek
            cubicTo(w * 0.95f, h * 0.65f, w * 0.8f, h * 0.85f, w * 0.5f, h * 0.85f)
            // Left cheek
            cubicTo(w * 0.2f, h * 0.85f, w * 0.05f, h * 0.65f, w * 0.15f, h * 0.45f)
        }
        
        val gradient = Brush.verticalGradient(
            colors = when (expression) {
                MascotExpression.SAD -> listOf(Color(0xFFE28A2B), Color(0xFFA0550B)) // Sad orange/brown
                MascotExpression.DISTRACTED -> listOf(Color(0xFFE28A2B), Color(0xFFA0550B))
                else -> listOf(Color(0xFF4ADE80), Color(0xFF15803D)) // Vibrant green Mascot
            }
        )
        drawPath(headPath, brush = gradient)
        
        // Outer dark thin stroke for definition
        drawPath(headPath, color = Color(0xFF0F170F), style = Stroke(width = 3.dp.toPx()))
        
        // Rosy cheeks
        if (expression != MascotExpression.SAD && expression != MascotExpression.DISTRACTED) {
            drawCircle(
                color = Color(0xFFFF6B81).copy(alpha = 0.5f),
                radius = w * 0.08f,
                center = Offset(w * 0.26f, h * 0.60f)
            )
            drawCircle(
                color = Color(0xFFFF6B81).copy(alpha = 0.5f),
                radius = w * 0.08f,
                center = Offset(w * 0.74f, h * 0.60f)
            )
        }
        
        // Eyes
        val eyeRadius = w * 0.09f
        val leftEyeCenter = Offset(w * 0.35f, h * 0.48f)
        val rightEyeCenter = Offset(w * 0.65f, h * 0.48f)
        
        when (expression) {
            MascotExpression.SAD, MascotExpression.DISTRACTED -> {
                // Sad eyes
                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.26f, h * 0.44f),
                    size = Size(w * 0.18f, h * 0.12f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.56f, h * 0.44f),
                    size = Size(w * 0.18f, h * 0.12f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Teardrop for SAD
                if (expression == MascotExpression.SAD) {
                    val tearPath = Path().apply {
                        moveTo(w * 0.26f, h * 0.58f)
                        quadraticTo(w * 0.22f, h * 0.7f, w * 0.26f, h * 0.74f)
                        quadraticTo(w * 0.30f, h * 0.74f, w * 0.30f, h * 0.7f)
                        close()
                    }
                    drawPath(tearPath, color = Color(0xFF60A5FA))
                }
            }
            else -> {
                // Cute sparkling eyes
                drawCircle(color = Color(0xFF1E293B), radius = eyeRadius, center = leftEyeCenter)
                drawCircle(color = Color(0xFF1E293B), radius = eyeRadius, center = rightEyeCenter)
                
                // Eyeballs sparkles
                drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(leftEyeCenter.x - 3f, leftEyeCenter.y - 3f))
                drawCircle(color = Color.White, radius = eyeRadius * 0.4f, center = Offset(rightEyeCenter.x - 3f, rightEyeCenter.y - 3f))
                drawCircle(color = Color.White, radius = eyeRadius * 0.18f, center = Offset(leftEyeCenter.x + 5f, leftEyeCenter.y + 5f))
                drawCircle(color = Color.White, radius = eyeRadius * 0.18f, center = Offset(rightEyeCenter.x + 5f, rightEyeCenter.y + 5f))
            }
        }
        
        // Smile / mouth
        val mouthPath = Path().apply {
            when (expression) {
                MascotExpression.SAD, MascotExpression.DISTRACTED -> {
                    moveTo(w * 0.44f, h * 0.68f)
                    quadraticTo(w * 0.5f, h * 0.62f, w * 0.56f, h * 0.68f)
                }
                else -> {
                    moveTo(w * 0.42f, h * 0.62f)
                    quadraticTo(w * 0.5f, h * 0.72f, w * 0.58f, h * 0.62f)
                }
            }
        }
        drawPath(
            path = mouthPath,
            color = Color(0xFF1E293B),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Extra hands / books
        if (expression == MascotExpression.STUDYING) {
            // Little hands holding yellow book
            val bookPath = Path().apply {
                moveTo(w * 0.32f, h * 0.76f)
                lineTo(w * 0.5f, h * 0.84f)
                lineTo(w * 0.68f, h * 0.76f)
                lineTo(w * 0.68f, h * 0.94f)
                lineTo(w * 0.5f, h * 0.99f)
                lineTo(w * 0.32f, h * 0.94f)
                close()
            }
            drawPath(bookPath, color = Color(0xFFFACC15))
            drawPath(bookPath, color = Color(0xFF0F170F), style = Stroke(width = 2.dp.toPx()))
            
            // Middle divider
            drawLine(
                color = Color(0xFFCA8A04),
                start = Offset(w * 0.5f, h * 0.84f),
                end = Offset(w * 0.5f, h * 0.99f),
                strokeWidth = 2.dp.toPx()
            )
        } else if (expression == MascotExpression.DISTRACTED) {
            // Holding grey smartphone
            val phonePath = Path().apply {
                moveTo(w * 0.40f, h * 0.72f)
                lineTo(w * 0.60f, h * 0.72f)
                lineTo(w * 0.60f, h * 0.96f)
                lineTo(w * 0.40f, h * 0.96f)
                close()
            }
            drawPath(phonePath, color = Color(0xFF475569))
            drawPath(phonePath, color = Color(0xFF0F170F), style = Stroke(width = 2.dp.toPx()))
            
            // Screen blue glow
            drawRect(
                color = Color(0xFF60A5FA),
                topLeft = Offset(w * 0.42f, h * 0.74f),
                size = Size(w * 0.16f, h * 0.16f)
            )
        }
    }
}

@Composable
fun PhoneMockup(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f),
        shape = RoundedCornerShape(36.dp),
        border = BorderStroke(3.dp, Color(0xFF222B22)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF070B07))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF050805)),
            content = content
        )
    }
}

@Composable
fun OnboardingDoodles() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Star 1
        val path1 = Path().apply {
            val cx = size.width * 0.10f
            val cy = size.height * 0.20f
            val r = 18f
            moveTo(cx, cy - r)
            quadraticTo(cx, cy, cx + r, cy)
            quadraticTo(cx, cy, cx, cy + r)
            quadraticTo(cx, cy, cx - r, cy)
            quadraticTo(cx, cy, cx, cy - r)
        }
        drawPath(path1, color = Color(0xFF10B981).copy(alpha = 0.25f))

        // Star 2
        val path2 = Path().apply {
            val cx = size.width * 0.88f
            val cy = size.height * 0.70f
            val r = 24f
            moveTo(cx, cy - r)
            quadraticTo(cx, cy, cx + r, cy)
            quadraticTo(cx, cy, cx, cy + r)
            quadraticTo(cx, cy, cx - r, cy)
            quadraticTo(cx, cy, cx, cy - r)
        }
        drawPath(path2, color = Color(0xFFFFD700).copy(alpha = 0.2f))

        // Curved elegant helper arrow doodle
        val arrowPath = Path().apply {
            val sx = size.width * 0.82f
            val sy = size.height * 0.15f
            moveTo(sx, sy)
            quadraticTo(sx - 40f, sy + 40f, sx - 20f, sy + 100f)
            moveTo(sx - 20f, sy + 100f)
            lineTo(sx - 35f, sy + 85f)
            moveTo(sx - 20f, sy + 100f)
            lineTo(sx + 5f, sy + 90f)
        }
        drawPath(
            path = arrowPath,
            color = Color(0xFF10B981).copy(alpha = 0.25f),
            style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
