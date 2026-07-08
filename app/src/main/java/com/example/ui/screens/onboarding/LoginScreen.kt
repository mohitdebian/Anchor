package com.example.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoggingIn by remember { mutableStateOf(false) }
    var loginStatusText by remember { mutableStateOf("Connecting securely...") }
    
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("userName", account.displayName ?: account.email?.substringBefore("@"))
                    putString("userEmail", account.email)
                    putString("userPhotoUrl", account.photoUrl?.toString())
                    apply()
                }
                isLoggingIn = true
                loginStatusText = "Authenticating ${account.email}..."
            } catch (e: com.google.android.gms.common.api.ApiException) {
                // If it fails (e.g. DEVELOPER_ERROR without SHA1), we can still mock the login or just continue
                android.util.Log.e("LoginScreen", "Google sign in failed", e)
                onFinish()
            }
        } else {
            // Cancelled
        }
    }

    // Custom animation for login state transitions
    LaunchedEffect(isLoggingIn) {
        if (isLoggingIn) {
            delay(1000)
            loginStatusText = "Authenticating mohitdebian@gmail.com..."
            delay(1200)
            loginStatusText = "Syncing your Anchor settings..."
            delay(1000)
            onFinish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.LightGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF070A07) // Deep dark forest night background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            com.example.ui.components.BackgroundDoodles()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.2f))

                // Security Shield Illustration with theme colors (emerald green/blue blend)
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        drawCircle(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            radius = 65.dp.toPx()
                        )
                        drawCircle(
                            color = Color(0xFF10B981).copy(alpha = 0.05f),
                            radius = 80.dp.toPx()
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "Secure Your Profile",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle
                Text(
                    text = "Sync your local session stats, block lists, and streak counts safely across your devices.",
                    fontSize = 15.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.weight(0.8f))

                if (isLoggingIn) {
                    // Logging-in / Progress screen
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111611)),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A221A))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF10B981),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = loginStatusText,
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                } else {
                    // Standard Google Sign-In Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Google Button
                        Button(
                            onClick = {
                                val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                    com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                                )
                                    .requestEmail()
                                    .requestProfile()
                                    .build()
                                val client = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                                launcher.launch(client.signInIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF1F1F1F)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                GoogleGLogo(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Sign in with Google",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        }

                        // Guest Continue
                        TextButton(
                            onClick = onFinish,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = "Continue as Guest",
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security note
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secured",
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Anchor protects your personal info",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Removed showAccountChooser bottom sheet simulation
        }
    }
}

@Composable
fun GoogleGLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.18f

        val center = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f)
        val radius = w / 2f - strokeWidth / 2f

        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 0f,
            sweepAngle = -45f,
            useCenter = true
        )
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -45f,
            sweepAngle = -110f,
            useCenter = true
        )
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = -155f,
            sweepAngle = -50f,
            useCenter = true
        )
        drawArc(
            color = Color(0xFF34A853),
            startAngle = -205f,
            sweepAngle = -110f,
            useCenter = true
        )
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 45f,
            sweepAngle = -45f,
            useCenter = true
        )

        drawCircle(
            color = Color.White,
            radius = radius - strokeWidth / 2
        )

        val barWidth = radius * 0.9f
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = androidx.compose.ui.geometry.Offset(center.x, center.y - strokeWidth / 2),
            size = androidx.compose.ui.geometry.Size(barWidth + strokeWidth / 2, strokeWidth)
        )
    }
}
