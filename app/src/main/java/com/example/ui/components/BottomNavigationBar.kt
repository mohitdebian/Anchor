package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToFocus: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(Color(0xFF1C1C1E))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                isSelected = currentRoute == "dashboard",
                onClick = onNavigateToFocus
            )
            BottomNavItem(
                icon = Icons.Default.Timer,
                isSelected = currentRoute == "planner",
                onClick = onNavigateToPlanner
            )
            BottomNavItem(
                icon = Icons.Default.Block,
                isSelected = currentRoute == "block",
                onClick = onNavigateToBlocks
            )
            BottomNavItem(
                icon = Icons.Default.Insights,
                isSelected = currentRoute == "analytics",
                onClick = onNavigateToAnalytics
            )
            BottomNavItem(
                icon = Icons.Default.AutoAwesome,
                isSelected = currentRoute == "insights",
                onClick = onNavigateToAIStats
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF163321) else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF10B981) else Color(0xFF8E8E93),
            modifier = Modifier.size(24.dp)
        )
    }
}
