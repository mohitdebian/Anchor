package com.example.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GlassCard
import com.example.viewmodels.AnalyticsViewModel
import com.example.viewmodels.AppViewModelProvider

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
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
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
            
            Text(
                text = "Weekly Trend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            WeeklyChart(data = uiState.weeklyData)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Sessions",
                    value = uiState.focusSessionsCount,
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Time Focused",
                    value = uiState.totalTimeSaved,
                    icon = Icons.Default.Timer,
                    iconColor = com.example.ui.theme.SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Text(
                text = "Most Distracting Apps",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            if (uiState.distractingApps.isEmpty()) {
                Text(
                    text = "No usage stats available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val maxMinutes = uiState.distractingApps.maxOfOrNull { it.timeInForegroundMinutes }?.toFloat() ?: 1f
                uiState.distractingApps.forEach { app ->
                    val h = app.timeInForegroundMinutes / 60
                    val m = app.timeInForegroundMinutes % 60
                    val timeString = if (h > 0) "${h}h ${m}m" else "${m}m"
                    val ratio = (app.timeInForegroundMinutes.toFloat() / maxMinutes).coerceIn(0f, 1f)
                    DistractingAppItem(app.appName, timeString, ratio)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        }
    }
}

@Composable
fun WeeklyChart(data: List<Float>) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val surfaceColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        
        Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            val barWidth = (size.width / data.size) * 0.6f
            val spacing = (size.width - (barWidth * data.size)) / (data.size - 1)
            
            data.forEachIndexed { index, value ->
                val x = index * (barWidth + spacing)
                val barHeight = size.height * value
                val y = size.height - barHeight
                
                // Background bar
                drawRoundRect(
                    color = surfaceColor,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(barWidth / 2)
                )
                
                // Value bar
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2)
                )
            }
        }
    }
}

@Composable
fun DistractingAppItem(name: String, time: String, ratio: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}
