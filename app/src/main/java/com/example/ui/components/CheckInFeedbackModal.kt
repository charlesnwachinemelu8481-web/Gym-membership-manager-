package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CheckInResult
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.ElectricLimeLight
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight

@Composable
fun CheckInFeedbackModal(
    result: CheckInResult?,
    onDismiss: () -> Unit,
    onRenewMember: (memberId: Long) -> Unit,
    onAdminOverride: (member: com.example.data.MemberEntity) -> Unit
) {
    if (result == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (result) {
                is CheckInResult.Success -> {
                    var animateIn by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (animateIn) 1f else 0.4f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "scale"
                    )

                    LaunchedEffect(Unit) {
                        animateIn = true
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().testTag("checkin_success_modal")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(ElectricLimeLight)
                                .border(3.dp, ElectricLime, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = Charcoal900,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ACCESS GRANTED!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal900
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Welcome, ${result.member.fullName}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )

                        Text(
                            text = "Member ID: ${result.member.memberCode} • ${result.member.planName}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricLimeLight)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "⚡ Check-in Verified • Have a great session!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF365314)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Charcoal900,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("checkin_success_done_button")
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                is CheckInResult.BlockedExpired -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().testTag("checkin_expired_modal")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(AlertRedLight)
                                .border(3.dp, AlertRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "Blocked",
                                tint = AlertRed,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "MEMBERSHIP EXPIRED",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = result.member.fullName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal900
                        )

                        Text(
                            text = "ID: ${result.member.memberCode} • Expired ${result.expiredDaysAgo} day(s) ago",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AlertRed,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFEF2F2))
                                .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Access is restricted. Front desk must process payment or grant an emergency 1-day pass.",
                                fontSize = 12.sp,
                                color = Color(0xFF991B1B),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Primary CTA: Renew Membership Now
                        Button(
                            onClick = {
                                onDismiss()
                                onRenewMember(result.member.id)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricLime,
                                contentColor = Charcoal900
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("checkin_renew_now_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Renew Membership Now", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Secondary Action: Admin 1-Day Pass Override
                        OutlinedButton(
                            onClick = {
                                onAdminOverride(result.member)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("checkin_admin_override_button")
                        ) {
                            Text("Admin 1-Day Pass Override", color = Charcoal900, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("checkin_dismiss_button")
                        ) {
                            Text("Cancel", color = Color(0xFF6B7280))
                        }
                    }
                }

                is CheckInResult.Overridden -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().testTag("checkin_override_modal")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(WarningAmberLight)
                                .border(3.dp, WarningAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Override",
                                tint = WarningAmber,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ADMIN OVERRIDE GRANTED",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal900
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${result.member.fullName} checked in under temporary front-desk waiver.",
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Charcoal900,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                is CheckInResult.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Check-In Error",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result.message,
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
