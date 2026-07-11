package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(androidx.compose.ui.graphics.Color(0xFF132018).copy(alpha = 0.75f))
            .border(
                width = 1.dp,
                color = androidx.compose.ui.graphics.Color(0xFF10B981).copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp)
            ),
        content = content
    )
}
