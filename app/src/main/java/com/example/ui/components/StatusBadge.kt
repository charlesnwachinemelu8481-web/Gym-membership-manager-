package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedDark
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeDark
import com.example.ui.theme.ElectricLimeLight
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight

@Composable
fun StatusBadge(
    member: MemberEntity,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val isActive = member.isMembershipActive(now)
    val daysRemaining = member.daysRemaining(now)

    val (bgColor, textColor, dotColor, text) = when {
        !isActive -> {
            val daysAgo = (-daysRemaining).coerceAtLeast(1)
            Quadruple(
                AlertRedLight,
                AlertRedDark,
                AlertRed,
                "Expired ${daysAgo}d ago"
            )
        }
        daysRemaining in 0..7 -> {
            val label = if (daysRemaining == 0L) "Expires Today" else "Expires in ${daysRemaining}d"
            Quadruple(
                WarningAmberLight,
                Color(0xFF92400E),
                WarningAmber,
                label
            )
        }
        else -> {
            Quadruple(
                ElectricLimeLight,
                ElectricLimeDark,
                ElectricLime,
                "Active"
            )
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
