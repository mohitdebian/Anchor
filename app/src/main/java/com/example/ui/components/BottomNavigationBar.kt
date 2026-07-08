package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToFocus: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFF0F0F0F))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Default.HourglassEmpty,
            label = "Focus",
            isSelected = currentRoute == "dashboard",
            onClick = onNavigateToFocus
        )
        BottomNavItem(
            icon = Icons.Default.CalendarToday,
            label = "Planner",
            isSelected = currentRoute == "planner",
            onClick = onNavigateToPlanner
        )
        BottomNavItem(
            icon = Icons.Default.Insights,
            label = "Analytics",
            isSelected = currentRoute == "analytics",
            onClick = onNavigateToAnalytics
        )
        BottomNavItem(
            icon = Icons.Default.AutoGraph,
            label = "AI Stats",
            isSelected = currentRoute == "insights",
            onClick = onNavigateToAIStats
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF4ADE80) else Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF4ADE80) else Color.White
        )
    }
}
