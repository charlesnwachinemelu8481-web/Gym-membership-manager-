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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckInEntity
import com.example.data.MemberEntity
import com.example.data.PaymentEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.Charcoal800
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeLight
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemberProfileScreen(
    member: MemberEntity,
    paymentHistory: List<PaymentEntity>,
    checkInHistory: List<CheckInEntity>,
    onRenewClick: (Long) -> Unit,
    onCheckInClick: (MemberEntity) -> Unit,
    onEditClick: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    val now = System.currentTimeMillis()
    val isActive = member.isMembershipActive(now)
    val daysRemaining = member.daysRemaining(now)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Surface(
                color = Charcoal900,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Charcoal800)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = member.memberCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime
                            )
                        }

                        // Edit Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Charcoal800)
                                .clickable { onEditClick(member.id) }
                                .testTag("profile_edit_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    MemberAvatar(
                        name = member.fullName,
                        colorIndex = member.avatarColorIndex,
                        size = 80.dp,
                        isActive = isActive
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = member.fullName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${member.phone} • ${member.email}",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StatusBadge(member = member)
                }
            }
        }

        // Action Buttons Row: Check-In & Renew
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onCheckInClick(member) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Charcoal900,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("profile_quick_checkin_button")
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Check In", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onRenewClick(member.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricLime,
                        contentColor = Charcoal900
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("profile_renew_membership_button")
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Renew Plan", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Plan Details & Expiry Countdown Gauge Card
        item {
            Surface(
                color = FitSurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACTIVE MEMBERSHIP PLAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = member.planName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal900
                            )
                            Text(
                                text = "Trainer: ${member.assignedTrainer}",
                                fontSize = 12.sp,
                                color = Color(0xFF4B5563)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isActive) ElectricLimeLight else AlertRedLight)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isActive) "${daysRemaining}d Left" else "Expired",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color(0xFF365314) else Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dates breakdown
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF9FAFB))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Enrolled Since", fontSize = 11.sp, color = Color(0xFF6B7280))
                            Text(
                                dateFormat.format(Date(member.startDate)),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal900
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Expires On", fontSize = 11.sp, color = Color(0xFF6B7280))
                            Text(
                                dateFormat.format(Date(member.expiryDate)),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Charcoal900 else AlertRed
                            )
                        }
                    }

                    if (!member.emergencyContact.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Emergency: ${member.emergencyContact}",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }

        // Tabs: Payment History & Check-In History
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Charcoal900,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Charcoal900
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Payments (${paymentHistory.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Check-Ins (${checkInHistory.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        // Tab Content
        if (selectedTab == 0) {
            if (paymentHistory.isEmpty()) {
                item {
                    EmptyHistoryCard("No recorded payments yet for this member.")
                }
            } else {
                items(paymentHistory) { payment ->
                    Surface(
                        color = FitSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFECFCCB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Receipt,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${payment.planName} (+${payment.extendedDays}d)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Charcoal900
                                    )
                                    Text(
                                        text = "${dateFormat.format(Date(payment.paymentDate))} • ${payment.paymentMethod}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            Text(
                                text = "$${String.format(Locale.US, "%.2f", payment.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }
        } else {
            if (checkInHistory.isEmpty()) {
                item {
                    EmptyHistoryCard("No facility check-in logs yet.")
                }
            } else {
                items(checkInHistory) { checkIn ->
                    Surface(
                        color = FitSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (checkIn.status == "OVERRIDDEN") Color(0xFFFEF3C7) else ElectricLimeLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = if (checkIn.status == "OVERRIDDEN") WarningAmber else Color(0xFF365314),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = dateFormat.format(Date(checkIn.timestamp)),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Charcoal900
                                    )
                                    Text(
                                        text = "${timeFormat.format(Date(checkIn.timestamp))} • ${checkIn.notes ?: "Turnstile Entry"}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (checkIn.status == "OVERRIDDEN") Color(0xFFFEF3C7) else Color(0xFFDCFCE7))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (checkIn.status == "OVERRIDDEN") "OVERRIDE" else "VERIFIED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (checkIn.status == "OVERRIDDEN") Color(0xFF92400E) else Color(0xFF166534)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun EmptyHistoryCard(message: String) {
    Surface(
        color = FitSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = message, fontSize = 13.sp, color = Color(0xFF6B7280))
        }
    }
}
