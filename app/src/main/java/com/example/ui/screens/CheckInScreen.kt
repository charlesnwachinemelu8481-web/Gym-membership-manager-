package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckInEntity
import com.example.data.MemberEntity
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Charcoal800
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CheckInScreen(
    members: List<MemberEntity>,
    todayCheckIns: List<CheckInEntity>,
    onCheckInMember: (MemberEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isScannerActive by remember { mutableStateOf(false) }
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    val filteredMembers = remember(searchQuery, members) {
        if (searchQuery.isBlank()) {
            members.take(8)
        } else {
            members.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                        it.memberCode.contains(searchQuery, ignoreCase = true) ||
                        it.phone.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scanner Banner / Mode Toggle
        item {
            Surface(
                color = Charcoal900,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CHECK-IN TERMINAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Instant Scan & Search",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { isScannerActive = !isScannerActive },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isScannerActive) AlertRed else Charcoal800,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("toggle_scanner_button")
                        ) {
                            Icon(
                                imageVector = if (isScannerActive) Icons.Default.Close else Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isScannerActive) "Close Scan" else "Simulate Scan", fontSize = 12.sp)
                        }
                    }

                    // Scanner Viewfinder
                    AnimatedVisibility(visible = isScannerActive) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(2.dp, ElectricLime, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scanner",
                                    tint = ElectricLime,
                                    modifier = Modifier.size(64.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(2.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color.Transparent, ElectricLime, Color.Transparent)
                                            )
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Align gym pass barcode or tap a member below for instant check-in verification",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search member by name or code (FA-1001)...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Charcoal900
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Charcoal900,
                    unfocusedBorderColor = FitBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkin_search_input")
            )
        }

        // Fast Tap Members List
        item {
            Text(
                text = if (searchQuery.isBlank()) "QUICK CHECK-IN DIRECTORY" else "SEARCH RESULTS (${filteredMembers.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280),
                letterSpacing = 1.sp
            )
        }

        items(filteredMembers, key = { it.id }) { member ->
            val now = System.currentTimeMillis()
            val isActive = member.isMembershipActive(now)

            Surface(
                color = FitSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkin_member_row_${member.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        MemberAvatar(
                            name = member.fullName,
                            colorIndex = member.avatarColorIndex,
                            size = 42.dp,
                            isActive = isActive
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = member.fullName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal900
                            )
                            Text(
                                text = "${member.memberCode} • ${member.planName}",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                            StatusBadge(member = member)
                        }
                    }

                    // One-Tap Check-In Button
                    Button(
                        onClick = { onCheckInMember(member) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) ElectricLime else AlertRed,
                            contentColor = if (isActive) Charcoal900 else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("checkin_button_${member.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isActive) "Check In" else "Verify",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Today's Live Check-Ins Stream
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S CHECK-IN LOG (${todayCheckIns.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )
            }
        }

        if (todayCheckIns.isEmpty()) {
            item {
                Surface(
                    color = FitSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No members checked in yet today.", fontSize = 13.sp, color = Color(0xFF6B7280))
                    }
                }
            }
        } else {
            items(todayCheckIns) { checkIn ->
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
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (checkIn.status == "OVERRIDDEN") Color(0xFFFEF3C7) else Color(0xFFECFCCB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = if (checkIn.status == "OVERRIDDEN") WarningAmber else Color(0xFF15803D),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = checkIn.memberName,
                                    fontSize = 13.sp,
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

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = timeFormat.format(Date(checkIn.timestamp)),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Charcoal900
                            )
                            Text(
                                text = if (checkIn.status == "OVERRIDDEN") "Override Pass" else "Verified Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (checkIn.status == "OVERRIDDEN") WarningAmber else Color(0xFF15803D)
                            )
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
