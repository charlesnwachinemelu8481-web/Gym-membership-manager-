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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

val PaymentMethods = listOf("Credit Card", "Cash", "Debit Card", "Mobile Pay", "Bank Wire")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    members: List<MemberEntity>,
    plans: List<PlanEntity>,
    preselectedMemberId: Long?,
    onSavePayment: (
        memberId: Long,
        planId: Long,
        amount: Double,
        paymentMethod: String,
        transactionRef: String,
        notes: String?
    ) -> Unit
) {
    var selectedMember by remember {
        mutableStateOf(members.firstOrNull { it.id == preselectedMemberId } ?: members.firstOrNull())
    }
    var memberDropdownExpanded by remember { mutableStateOf(false) }

    var selectedPlan by remember {
        mutableStateOf(
            plans.firstOrNull { it.id == selectedMember?.planId } ?: plans.firstOrNull()
        )
    }
    var planDropdownExpanded by remember { mutableStateOf(false) }

    var amountStr by remember { mutableStateOf(selectedPlan?.price?.toString() ?: "75.00") }
    var selectedMethod by remember { mutableStateOf(PaymentMethods[0]) }
    var notes by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Update amount if plan changes
    LaunchedEffect(selectedPlan) {
        selectedPlan?.let {
            amountStr = String.format(Locale.US, "%.2f", it.price)
        }
    }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val now = System.currentTimeMillis()

    // Calculate auto-extended date
    val currentExpiry = selectedMember?.expiryDate ?: now
    val baseDate = if (currentExpiry > now) currentExpiry else now
    val durationMs = (selectedPlan?.durationDays ?: 30) * 24L * 60L * 60L * 1000L
    val newExpiryDate = baseDate + durationMs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Member Selection Card
        Surface(
            color = FitSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SELECT GYM MEMBER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )

                ExposedDropdownMenuBox(
                    expanded = memberDropdownExpanded,
                    onExpandedChange = { memberDropdownExpanded = !memberDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedMember != null) "${selectedMember!!.fullName} (${selectedMember!!.memberCode})" else "Select Member",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Charcoal900)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Charcoal900,
                            unfocusedBorderColor = FitBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("payment_member_select")
                    )

                    ExposedDropdownMenu(
                        expanded = memberDropdownExpanded,
                        onDismissRequest = { memberDropdownExpanded = false }
                    ) {
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        MemberAvatar(name = m.fullName, colorIndex = m.avatarColorIndex, size = 32.dp, showStatusDot = false)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(m.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${m.memberCode} • ${m.planName}", fontSize = 11.sp, color = Color(0xFF6B7280))
                                        }
                                    }
                                },
                                onClick = {
                                    selectedMember = m
                                    selectedPlan = plans.firstOrNull { it.id == m.planId } ?: selectedPlan
                                    memberDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Plan & Amount Card
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
                    text = "MEMBERSHIP PLAN & PRICING",
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
                        value = "${selectedPlan?.name ?: "Select Plan"} ($${selectedPlan?.price ?: 0} / ${selectedPlan?.durationLabel ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Charcoal900,
                            unfocusedBorderColor = FitBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("payment_plan_select")
                    )

                    ExposedDropdownMenu(
                        expanded = planDropdownExpanded,
                        onDismissRequest = { planDropdownExpanded = false }
                    ) {
                        plans.forEach { p ->
                            DropdownMenuItem(
                                text = {
                                    Text("${p.name} — $${p.price} (${p.durationLabel})", fontSize = 13.sp)
                                },
                                onClick = {
                                    selectedPlan = p
                                    planDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Payment Amount ($)") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF15803D))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Charcoal900,
                        unfocusedBorderColor = FitBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_amount_input")
                )
            }
        }

        // Auto-Extension Preview Box
        Surface(
            color = Color(0xFFF3F4F6),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Charcoal900,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUTOMATIC EXPIRY EXTENSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal900
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Expiry", fontSize = 10.sp, color = Color(0xFF6B7280))
                        Text(
                            text = dateFormat.format(Date(currentExpiry)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Charcoal900
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = ElectricLime,
                        modifier = Modifier.size(18.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text("New Expiry Date (+${selectedPlan?.durationDays ?: 30}d)", fontSize = 10.sp, color = Color(0xFF15803D))
                        Text(
                            text = dateFormat.format(Date(newExpiryDate)),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }
        }

        // Payment Method Selector
        Surface(
            color = FitSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, FitBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PAYMENT METHOD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PaymentMethods) { method ->
                        val isSelected = method == selectedMethod
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Charcoal900 else Color(0xFFF3F4F6))
                                .clickable { selectedMethod = method }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("payment_method_$method")
                        ) {
                            Text(
                                text = method,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ElectricLime else Charcoal900
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Reference (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Charcoal900,
                        unfocusedBorderColor = FitBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (errorMsg != null) {
            Text(text = errorMsg ?: "", color = AlertRed, fontSize = 12.sp)
        }

        // Save & Process Payment Button
        Button(
            onClick = {
                val member = selectedMember
                val plan = selectedPlan
                val amount = amountStr.toDoubleOrNull()

                if (member == null) {
                    errorMsg = "Please select a member"
                    return@Button
                }
                if (plan == null) {
                    errorMsg = "Please select a plan"
                    return@Button
                }
                if (amount == null || amount <= 0) {
                    errorMsg = "Please enter a valid amount"
                    return@Button
                }

                val ref = "TXN-${UUID.randomUUID().toString().take(6).uppercase()}"
                onSavePayment(
                    member.id,
                    plan.id,
                    amount,
                    selectedMethod,
                    ref,
                    notes.ifBlank { null }
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
                .testTag("save_payment_button")
        ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVE & ISSUE RECEIPT",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
