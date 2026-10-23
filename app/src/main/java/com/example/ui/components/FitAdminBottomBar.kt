package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Charcoal800
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime

@Composable
fun FitAdminBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    expiringCount: Int = 0
) {
    Surface(
        color = Charcoal900,
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dashboard Tab
            BottomNavItem(
                icon = Icons.Default.SpaceDashboard,
                label = "Dashboard",
                isSelected = currentScreen is Screen.Dashboard,
                testTag = "nav_dashboard",
                onClick = { onNavigate(Screen.Dashboard) }
            )

            // Members Tab
            BottomNavItem(
                icon = Icons.Default.People,
                label = "Members",
                isSelected = currentScreen is Screen.MembersList,
                testTag = "nav_members",
                onClick = { onNavigate(Screen.MembersList) }
            )

            // Prominent Check-In Central Button
            Box(
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .size(54.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(ElectricLime)
                    .clickable { onNavigate(Screen.CheckIn) }
                    .testTag("nav_quick_checkin"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Quick Check-In",
                    tint = Charcoal900,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Expiring Tab
            BottomNavItem(
                icon = Icons.Default.HourglassTop,
                label = "Expiring",
                isSelected = currentScreen is Screen.ExpiringSoon,
                badgeCount = expiringCount,
                testTag = "nav_expiring",
                onClick = { onNavigate(Screen.ExpiringSoon) }
            )

            // Plans Tab
            BottomNavItem(
                icon = Icons.Default.Inventory2,
                label = "Plans",
                isSelected = currentScreen is Screen.PlansSetup,
                testTag = "nav_plans",
                onClick = { onNavigate(Screen.PlansSetup) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = AlertRed,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ElectricLime else Color(0xFF9CA3AF),
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ElectricLime else Color(0xFF9CA3AF),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
