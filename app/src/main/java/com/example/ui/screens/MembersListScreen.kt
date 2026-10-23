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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberEntity
import com.example.data.PlanEntity
import com.example.ui.MemberStatusFilter
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.Charcoal700
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MembersListScreen(
    members: List<MemberEntity>,
    plans: List<PlanEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    statusFilter: MemberStatusFilter,
    onStatusFilterChange: (MemberStatusFilter) -> Unit,
    selectedPlanId: Long?,
    onPlanFilterChange: (Long?) -> Unit,
    onSelectMember: (Long) -> Unit,
    onAddNewMember: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // Search and Actions Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MEMBERS DIRECTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${members.size} Enrolled Records",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal900
                    )
                }

                Button(
                    onClick = onAddNewMember,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricLime,
                        contentColor = Charcoal900
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("add_member_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Member",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by name, code (FA-1001), phone...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Charcoal900,
                    unfocusedBorderColor = FitBorder,
                    focusedContainerColor = Color(0xFFF9FAFB),
                    unfocusedContainerColor = Color(0xFFF9FAFB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("members_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    StatusChip(
                        label = "All (${members.size})",
                        isSelected = statusFilter == MemberStatusFilter.ALL,
                        onClick = { onStatusFilterChange(MemberStatusFilter.ALL) }
                    )
                }
                item {
                    StatusChip(
                        label = "Active",
                        isSelected = statusFilter == MemberStatusFilter.ACTIVE,
                        onClick = { onStatusFilterChange(MemberStatusFilter.ACTIVE) }
                    )
                }
                item {
                    StatusChip(
                        label = "Expiring (7d)",
                        isSelected = statusFilter == MemberStatusFilter.EXPIRING_SOON,
                        onClick = { onStatusFilterChange(MemberStatusFilter.EXPIRING_SOON) }
                    )
                }
                item {
                    StatusChip(
                        label = "Expired",
                        isSelected = statusFilter == MemberStatusFilter.EXPIRED,
                        onClick = { onStatusFilterChange(MemberStatusFilter.EXPIRED) }
                    )
                }
            }
        }

        // Table Header
        Surface(
            color = Color(0xFFF3F4F6),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEMBER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.weight(1.8f)
                )
                Text(
                    text = "PLAN / EXPIRY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.weight(1.4f)
                )
                Text(
                    text = "STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.width(90.dp)
                )
            }
        }

        // Members List (Data Table + Card Hybrid)
        if (members.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching gym members found",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your search terms or filters",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    MemberRowCard(
                        member = member,
                        dateFormat = dateFormat,
                        onClick = { onSelectMember(member.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Charcoal900 else Color(0xFFF3F4F6))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ElectricLime else Color(0xFF4B5563)
        )
    }
}

@Composable
private fun MemberRowCard(
    member: MemberEntity,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val isActive = member.isMembershipActive(now)

    Surface(
        color = FitSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("member_row_${member.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Member info col
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.8f)
            ) {
                MemberAvatar(
                    name = member.fullName,
                    colorIndex = member.avatarColorIndex,
                    size = 40.dp,
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
                        text = "${member.memberCode} • ${member.phone}",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Plan / Expiry col
            Column(modifier = Modifier.weight(1.4f)) {
                Text(
                    text = member.planName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Charcoal900
                )
                Text(
                    text = "Exp: ${dateFormat.format(Date(member.expiryDate))}",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
            }

            // Status Badge
            Box(
                modifier = Modifier.width(90.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                StatusBadge(member = member)
            }
        }
    }
}
