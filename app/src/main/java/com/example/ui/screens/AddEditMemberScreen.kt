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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.MemberEntity
import com.example.data.PlanEntity
import com.example.ui.components.AvatarPalette
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val GymTrainers = listOf(
    "None / Self-Guided",
    "Marcus Vance (Senior Coach)",
    "Elena Rostova (HIIT & Strength)",
    "Devon Cole (Powerlifting & PT)",
    "Sarah Jenkins (Mobility & Pilates)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberScreen(
    existingMember: MemberEntity?,
    plans: List<PlanEntity>,
    onSave: (
        id: Long,
        fullName: String,
        phone: String,
        email: String,
        plan: PlanEntity,
        startDate: Long,
        assignedTrainer: String,
        emergencyContact: String?,
        avatarColorIndex: Int
    ) -> Unit,
    onDelete: ((MemberEntity) -> Unit)? = null
) {
    var fullName by remember { mutableStateOf(existingMember?.fullName ?: "") }
    var phone by remember { mutableStateOf(existingMember?.phone ?: "") }
    var email by remember { mutableStateOf(existingMember?.email ?: "") }
    var selectedPlan by remember {
        mutableStateOf(
            plans.firstOrNull { it.id == existingMember?.planId }
                ?: plans.firstOrNull()
                ?: PlanEntity(1, "Pro Fitness", 30, "1 Month", 75.0, "Full access", "Gym, Classes")
        )
    }
    var assignedTrainer by remember {
        mutableStateOf(existingMember?.assignedTrainer ?: GymTrainers.first())
    }
    var emergencyContact by remember { mutableStateOf(existingMember?.emergencyContact ?: "") }
    var avatarColorIndex by remember { mutableIntStateOf(existingMember?.avatarColorIndex ?: 0) }
    var startDate by remember { mutableLongStateOf(existingMember?.startDate ?: System.currentTimeMillis()) }

    var trainerDropdownExpanded by remember { mutableStateOf(false) }
    var planDropdownExpanded by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Avatar & Color Theme
        Surface(
            color = FitSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MemberAvatar(
                    name = fullName.ifBlank { "New Member" },
                    colorIndex = avatarColorIndex,
                    size = 72.dp,
                    showStatusDot = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Member Avatar Badge Color",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AvatarPalette.size) { index ->
                        val isSelected = index == avatarColorIndex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AvatarPalette[index])
                                .clickable { avatarColorIndex = index }
                                .then(
                                    if (isSelected) {
                                        Modifier.border(2.5.dp, Charcoal900, CircleShape)
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Personal Details
        Surface(
            color = FitSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "PERSONAL INFORMATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; formError = null },
                    label = { Text("Full Name *") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Charcoal900)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Charcoal900,
                        unfocusedBorderColor = FitBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; formError = null },
                    label = { Text("Phone Number *") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Charcoal900)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Charcoal900,
                        unfocusedBorderColor = FitBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_phone_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Charcoal900)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Charcoal900,
                        unfocusedBorderColor = FitBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_email_input")
                )

                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Contact (Name & Phone)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Charcoal900,
                        unfocusedBorderColor = FitBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section: Membership Plan & Trainer
        Surface(
            color = FitSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "MEMBERSHIP & COACHING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )

                // Plan Dropdown
                ExposedDropdownMenuBox(
                    expanded = planDropdownExpanded,
                    onExpandedChange = { planDropdownExpanded = !planDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedPlan.name} ($${selectedPlan.price} / ${selectedPlan.durationLabel})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selected Membership Plan *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Charcoal900,
                            unfocusedBorderColor = FitBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("plan_select_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = planDropdownExpanded,
                        onDismissRequest = { planDropdownExpanded = false }
                    ) {
                        plans.forEach { plan ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            "$${plan.price} • ${plan.durationLabel} • ${plan.description}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedPlan = plan
                                    planDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Trainer Dropdown
                ExposedDropdownMenuBox(
                    expanded = trainerDropdownExpanded,
                    onExpandedChange = { trainerDropdownExpanded = !trainerDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = assignedTrainer,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned Trainer (Optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = trainerDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Charcoal900,
                            unfocusedBorderColor = FitBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = trainerDropdownExpanded,
                        onDismissRequest = { trainerDropdownExpanded = false }
                    ) {
                        GymTrainers.forEach { trainer ->
                            DropdownMenuItem(
                                text = { Text(trainer, fontSize = 13.sp) },
                                onClick = {
                                    assignedTrainer = trainer
                                    trainerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Start Date Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Membership Start Date", fontSize = 12.sp, color = Color(0xFF4B5563))
                    Text(
                        dateFormat.format(Date(startDate)),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal900
                    )
                }
            }
        }

        if (formError != null) {
            Text(
                text = formError ?: "",
                color = AlertRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Save Button
        Button(
            onClick = {
                if (fullName.isBlank()) {
                    formError = "Please enter member's full name"
                    return@Button
                }
                if (phone.isBlank()) {
                    formError = "Please enter phone number"
                    return@Button
                }

                onSave(
                    existingMember?.id ?: 0L,
                    fullName,
                    phone,
                    email,
                    selectedPlan,
                    startDate,
                    assignedTrainer,
                    emergencyContact,
                    avatarColorIndex
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricLime,
                contentColor = Charcoal900
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_member_button")
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (existingMember == null) "SAVE & ENROLL MEMBER" else "UPDATE MEMBER PROFILE",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // Delete button if existing
        if (existingMember != null && onDelete != null) {
            OutlinedButton(
                onClick = { onDelete(existingMember) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("delete_member_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete Member Record", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
