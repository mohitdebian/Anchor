package com.example.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlowingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge.copy(color = contentColor),
            content = { content() }
        )
    }
}
