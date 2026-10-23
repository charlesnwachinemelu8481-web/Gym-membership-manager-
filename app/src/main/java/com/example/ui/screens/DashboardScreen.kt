package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckInEntity
import com.example.data.MemberEntity
import com.example.ui.DashboardStats
import com.example.ui.Screen
import com.example.ui.StaffUser
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.Charcoal800
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeLight
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    recentCheckIns: List<CheckInEntity>,
    currentUser: StaffUser,
    onNavigate: (Screen) -> Unit,
    onSelectMember: (Long) -> Unit
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Front Desk Status Bar
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = Charcoal900,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElectricLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STATION ONLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Desk Lead: ${currentUser.name}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${currentUser.role} • Shift Active",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    // Prominent Quick Check-In Button
                    Button(
                        onClick = { onNavigate(Screen.CheckIn) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricLime,
                            contentColor = Charcoal900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("dashboard_quick_checkin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHECK-IN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 4 Key Stat Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Members",
                        value = stats.activeMembersCount.toString(),
                        subtitle = "+3 joined this week",
                        icon = Icons.Default.People,
                        iconBgColor = ElectricLimeLight,
                        iconTint = Color(0xFF365314),
                        testTag = "stat_active_members",
                        onClick = { onNavigate(Screen.MembersList) },
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Expiring (7d)",
                        value = stats.expiringThisWeekCount.toString(),
                        subtitle = "Needs action",
                        icon = Icons.Default.HourglassTop,
                        iconBgColor = if (stats.expiringThisWeekCount > 0) WarningAmberLight else FitBorder,
                        iconTint = if (stats.expiringThisWeekCount > 0) WarningAmber else Color.Gray,
                        accentBorder = if (stats.expiringThisWeekCount > 0) WarningAmber else null,
                        testTag = "stat_expiring_week",
                        onClick = { onNavigate(Screen.ExpiringSoon) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Revenue (Month)",
                        value = "$${String.format(Locale.US, "%,.0f", stats.revenueThisMonth)}",
                        subtitle = "Target: $18,000",
                        icon = Icons.Default.AttachMoney,
                        iconBgColor = Color(0xFFECFCCB),
                        iconTint = Color(0xFF15803D),
                        testTag = "stat_revenue_month",
                        onClick = { onNavigate(Screen.RecordPayment()) },
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Check-ins Today",
                        value = stats.checkInsTodayCount.toString(),
                        subtitle = "Facility peak: 6-8 PM",
                        icon = Icons.Default.FitnessCenter,
                        iconBgColor = Color(0xFFDBEAFE),
                        iconTint = Color(0xFF1D4ED8),
                        testTag = "stat_checkins_today",
                        onClick = { onNavigate(Screen.CheckIn) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        label = "+ Member",
                        icon = Icons.Default.PersonAdd,
                        testTag = "quick_action_add_member",
                        onClick = { onNavigate(Screen.AddEditMember()) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Payment",
                        icon = Icons.Default.Payment,
                        testTag = "quick_action_payment",
                        onClick = { onNavigate(Screen.RecordPayment()) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Expiring",
                        icon = Icons.Default.HourglassTop,
                        testTag = "quick_action_expiring",
                        onClick = { onNavigate(Screen.ExpiringSoon) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        label = "Plans",
                        icon = Icons.Default.Inventory2,
                        testTag = "quick_action_plans",
                        onClick = { onNavigate(Screen.PlansSetup) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Live Facility Check-In Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT CHECK-INS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "See Check-In Console",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Charcoal900,
                    modifier = Modifier.clickable { onNavigate(Screen.CheckIn) }
                )
            }
        }

        if (recentCheckIns.isEmpty()) {
            item {
                Surface(
                    color = FitSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No check-ins logged yet today",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        } else {
            items(recentCheckIns.take(6)) { checkIn ->
                Surface(
                    color = FitSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectMember(checkIn.memberId) }
                        .testTag("recent_checkin_item_${checkIn.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MemberAvatar(
                                name = checkIn.memberName,
                                size = 38.dp,
                                showStatusDot = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = checkIn.memberName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Charcoal900
                                )
                                Text(
                                    text = "${checkIn.memberCode} • ${checkIn.planName}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = timeFormat.format(Date(checkIn.timestamp)),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Charcoal900
                                )
                                Text(
                                    text = if (checkIn.status == "OVERRIDDEN") "Override" else "Verified",
                                    fontSize = 10.sp,
                                    color = if (checkIn.status == "OVERRIDDEN") WarningAmber else Color(0xFF15803D),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = FitSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Charcoal900,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Charcoal900
            )
        }
    }
}
