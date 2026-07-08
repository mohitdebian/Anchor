package com.example.ui.screens.insights

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GlassCard
import com.example.viewmodels.AppViewModelProvider
import com.example.viewmodels.InsightsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateBack: () -> Unit,
    viewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            com.example.ui.components.BackgroundDoodles()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Powered by NVIDIA NIM",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (uiState.error != null) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else if (uiState.insights.isNotEmpty()) {
                    uiState.insights.forEach { insight ->
                        InsightCard(insight = insight)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else if (uiState.genericInsight.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Pristine",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Zero distractions detected!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.genericInsight,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InsightCard(insight: com.example.viewmodels.AppInsight) {
    val context = LocalContext.current
    val pm = context.packageManager
    val iconBitmap = remember(insight.packageName) {
        try {
            val drawable = pm.getApplicationIcon(insight.packageName)
            drawable.toBitmap(width = 120, height = 120).asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap,
                            contentDescription = insight.appName,
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(insight.appName.take(1), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = insight.appName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Unproductive Session",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
                
                Surface(
                    color = Color(0xFFD32F2F).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${insight.timeSpentMinutes} mins wasted",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = insight.roast,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                lineHeight = 20.sp
            )
        }
    }
}
