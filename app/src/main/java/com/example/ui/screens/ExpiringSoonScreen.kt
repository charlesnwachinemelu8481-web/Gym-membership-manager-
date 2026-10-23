package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpiringSoonScreen(
    expiringMembers: List<MemberEntity>,
    onSendReminder: (MemberEntity) -> Unit,
    onRenewClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
        ) {
            // Header Card
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RETENTION & RENEWALS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${expiringMembers.size} Expiring Within 7 Days",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal900
                            )
                        }

                        // Bulk Reminder button
                        Button(
                            onClick = {
                                expiringMembers.forEach { onSendReminder(it) }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Dispatched reminder alerts to ${expiringMembers.size} members")
                                }
                            },
                            enabled = expiringMembers.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Charcoal900,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("send_all_reminders_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Notify All", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // List of Expiring Members
            if (expiringMembers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFECFCCB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "All Memberships Are Up To Date!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No memberships are expiring within the next 7 days.",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(expiringMembers, key = { it.id }) { member ->
                        val now = System.currentTimeMillis()
                        val daysRemaining = member.daysRemaining(now)

                        Surface(
                            color = FitSurface,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("expiring_member_card_${member.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        MemberAvatar(
                                            name = member.fullName,
                                            colorIndex = member.avatarColorIndex,
                                            size = 44.dp,
                                            isActive = member.isMembershipActive(now)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = member.fullName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Charcoal900
                                            )
                                            Text(
                                                text = "${member.memberCode} • ${member.phone}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF6B7280)
                                            )
                                        }
                                    }

                                    StatusBadge(member = member)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Plan & Expiry Details
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF9FAFB))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Current Tier", fontSize = 10.sp, color = Color(0xFF6B7280))
                                        Text(member.planName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Charcoal900)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Expiration Date", fontSize = 10.sp, color = Color(0xFF6B7280))
                                        Text(
                                            dateFormat.format(Date(member.expiryDate)),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (daysRemaining <= 0) AlertRed else WarningAmber
                                        )
                                    }
                                }

                                if (member.reminderSentCount > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✓ Reminder sent ${member.reminderSentCount} time(s)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Actions: Send Reminder & Renew
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Send Reminder Action
                                    OutlinedButton(
                                        onClick = {
                                            onSendReminder(member)
                                            // Launch Share Intent with pre-composed reminder
                                            val reminderMsg = "Hi ${member.fullName}, your FitAdmin gym membership (${member.planName}) is set to expire on ${dateFormat.format(Date(member.expiryDate))}. Renew today at the front desk to keep your streak!"
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, reminderMsg)
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, "Send Reminder to ${member.fullName}")
                                            context.startActivity(shareIntent)

                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Reminder logged for ${member.fullName}")
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .testTag("send_reminder_btn_${member.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = Charcoal900,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Send Reminder", color = Charcoal900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    // Renew Action
                                    Button(
                                        onClick = { onRenewClick(member.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricLime,
                                            contentColor = Charcoal900
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .testTag("renew_member_btn_${member.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payment,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Renew Plan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
