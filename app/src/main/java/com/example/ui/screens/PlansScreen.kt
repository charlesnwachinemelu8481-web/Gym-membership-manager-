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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.MemberEntity
import com.example.data.PlanEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Charcoal900
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.FitBorder
import com.example.ui.theme.FitSurface
import java.util.Locale

@Composable
fun PlansScreen(
    plans: List<PlanEntity>,
    members: List<MemberEntity>,
    onSavePlan: (
        id: Long,
        name: String,
        durationDays: Int,
        durationLabel: String,
        price: Double,
        description: String,
        perks: String,
        colorHex: String
    ) -> Unit,
    onDeletePlan: (PlanEntity) -> Unit
) {
    var showPlanDialog by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<PlanEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // Header
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MEMBERSHIP TIERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Facility Plans & Pricing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal900
                    )
                }

                Button(
                    onClick = {
                        editingPlan = null
                        showPlanDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricLime,
                        contentColor = Charcoal900
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("add_plan_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Plan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Plans List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(plans, key = { it.id }) { plan ->
                val enrolledCount = members.count { it.planId == plan.id }
                PlanCard(
                    plan = plan,
                    enrolledCount = enrolledCount,
                    onEdit = {
                        editingPlan = plan
                        showPlanDialog = true
                    },
                    onDelete = { onDeletePlan(plan) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showPlanDialog) {
        PlanDialog(
            plan = editingPlan,
            onDismiss = { showPlanDialog = false },
            onConfirm = { id, name, days, label, price, desc, perks, colorHex ->
                onSavePlan(id, name, days, label, price, desc, perks, colorHex)
                showPlanDialog = false
            }
        )
    }
}

@Composable
private fun PlanCard(
    plan: PlanEntity,
    enrolledCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = FitSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (plan.isPopular) 2.dp else 1.dp,
            if (plan.isPopular) ElectricLime else FitBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal900
                        )
                        if (plan.isPopular) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ElectricLime)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MOST POPULAR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Charcoal900
                                )
                            }
                        }
                    }

                    Text(
                        text = plan.description,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Charcoal900, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Duration bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9FAFB))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", plan.price)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal900
                    )
                    Text(
                        text = " / ${plan.durationLabel}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE0E7FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$enrolledCount Active Member${if (enrolledCount == 1) "" else "s"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3730A3)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Perks tags
            Text(
                text = "INCLUDED BENEFITS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9CA3AF),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            plan.perkList().forEach { perk ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = ElectricLime,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = perk, fontSize = 12.sp, color = Color(0xFF374151))
                }
            }
        }
    }
}

@Composable
private fun PlanDialog(
    plan: PlanEntity?,
    onDismiss: () -> Unit,
    onConfirm: (
        id: Long,
        name: String,
        durationDays: Int,
        durationLabel: String,
        price: Double,
        description: String,
        perks: String,
        colorHex: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(plan?.name ?: "") }
    var priceStr by remember { mutableStateOf(plan?.price?.toString() ?: "50.0") }
    var durationDaysStr by remember { mutableStateOf(plan?.durationDays?.toString() ?: "30") }
    var durationLabel by remember { mutableStateOf(plan?.durationLabel ?: "1 Month") }
    var description by remember { mutableStateOf(plan?.description ?: "") }
    var perks by remember { mutableStateOf(plan?.perks ?: "Gym Floor, Locker Room") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (plan == null) "Create Membership Plan" else "Edit Plan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal900
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plan Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("plan_name_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price ($) *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("plan_price_input")
                    )

                    OutlinedTextField(
                        value = durationDaysStr,
                        onValueChange = { durationDaysStr = it },
                        label = { Text("Days *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("plan_days_input")
                    )
                }

                OutlinedTextField(
                    value = durationLabel,
                    onValueChange = { durationLabel = it },
                    label = { Text("Duration Label (e.g. 1 Month)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = perks,
                    onValueChange = { perks = it },
                    label = { Text("Perks (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(text = error ?: "", color = AlertRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF6B7280))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = priceStr.toDoubleOrNull()
                            val days = durationDaysStr.toIntOrNull()
                            if (name.isBlank() || price == null || days == null) {
                                error = "Please fill in valid name, price, and days"
                                return@Button
                            }
                            onConfirm(
                                plan?.id ?: 0L,
                                name,
                                days,
                                durationLabel,
                                price,
                                description,
                                perks,
                                "#84CC16"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricLime,
                            contentColor = Charcoal900
                        ),
                        modifier = Modifier.testTag("plan_save_button")
                    ) {
                        Text("Save Plan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
