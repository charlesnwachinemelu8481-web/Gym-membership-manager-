package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricLime

val AvatarPalette = listOf(
    Color(0xFF0F766E), // Teal
    Color(0xFF1E3A8A), // Navy
    Color(0xFFB45309), // Amber
    Color(0xFF4C1D95), // Indigo
    Color(0xFF831843), // Crimson
    Color(0xFF15803D), // Forest
    Color(0xFF1E293B), // Slate
    Color(0xFFC2410C), // Orange
    Color(0xFF0284C7), // Sky
    Color(0xFF701A75)  // Fuchsia
)

@Composable
fun MemberAvatar(
    name: String,
    colorIndex: Int = 0,
    size: Dp = 44.dp,
    showStatusDot: Boolean = true,
    isActive: Boolean = true,
    modifier: Modifier = Modifier
) {
    val initials = name.trim().split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifEmpty { "M" }

    val bgColor = AvatarPalette[colorIndex.coerceIn(0, AvatarPalette.lastIndex)]
    val fontSize = (size.value * 0.38).sp

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (showStatusDot) {
            val dotSize = (size.value * 0.28).dp.coerceAtLeast(8.dp)
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(1.5.dp)
                    .clip(CircleShape)
                    .background(if (isActive) ElectricLime else AlertRed)
            )
        }
    }
}
