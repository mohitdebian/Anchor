package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun BackgroundDoodles() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Star 1
        val path = Path().apply {
            val cx = size.width * 0.15f
            val cy = size.height * 0.15f
            val r = 24f
            moveTo(cx, cy - r)
            quadraticTo(cx, cy, cx + r, cy)
            quadraticTo(cx, cy, cx, cy + r)
            quadraticTo(cx, cy, cx - r, cy)
            quadraticTo(cx, cy, cx, cy - r)
        }
        drawPath(path, color = Color(0xFF3B82F6).copy(alpha = 0.5f))

        // Star 2
        val path2 = Path().apply {
            val cx = size.width * 0.85f
            val cy = size.height * 0.45f
            val r = 16f
            moveTo(cx, cy - r)
            quadraticTo(cx, cy, cx + r, cy)
            quadraticTo(cx, cy, cx, cy + r)
            quadraticTo(cx, cy, cx - r, cy)
            quadraticTo(cx, cy, cx, cy - r)
        }
        drawPath(path2, color = Color(0xFFFFD700).copy(alpha = 0.5f))

        // Arrow
        val arrowPath = Path().apply {
            val startX = size.width * 0.8f
            val startY = size.height * 0.1f
            moveTo(startX, startY)
            quadraticTo(startX - 50f, startY + 50f, startX - 20f, startY + 120f)
            moveTo(startX - 20f, startY + 120f)
            lineTo(startX - 40f, startY + 100f)
            moveTo(startX - 20f, startY + 120f)
            lineTo(startX + 10f, startY + 110f)
        }
        drawPath(arrowPath, color = Color(0xFF3B82F6).copy(alpha = 0.3f), style = Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))

        // Wave
        val wavePath = Path().apply {
            val startX = size.width * 0.1f
            val startY = size.height * 0.8f
            moveTo(startX, startY)
            quadraticTo(startX + 50f, startY - 50f, startX + 100f, startY)
            quadraticTo(startX + 150f, startY + 50f, startX + 200f, startY)
        }
        drawPath(wavePath, color = Color(0xFF10B981).copy(alpha = 0.3f), style = Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    }
}
