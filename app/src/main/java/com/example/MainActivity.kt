package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.MemberEntity
import com.example.ui.FitAdminViewModel
import com.example.ui.Screen
import com.example.ui.components.CheckInFeedbackModal
import com.example.ui.components.FitAdminBottomBar
import com.example.ui.components.FitAdminTopBar
import com.example.ui.components.PaymentReceiptModal
import com.example.ui.screens.AddEditMemberScreen
import com.example.ui.screens.CheckInScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpiringSoonScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemberProfileScreen
import com.example.ui.screens.MembersListScreen
import com.example.ui.screens.PlansScreen
import com.example.ui.screens.RecordPaymentScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: FitAdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FitAdminApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FitAdminApp(viewModel: FitAdminViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val allMembers by viewModel.allMembers.collectAsState()
    val filteredMembers by viewModel.filteredMembers.collectAsState()
    val allPlans by viewModel.allPlans.collectAsState()
    val dashboardStats by viewModel.dashboardStats.collectAsState()
    val todayCheckIns by viewModel.todayCheckIns.collectAsState()
    val allCheckIns by viewModel.allCheckIns.collectAsState()
    val expiringMembers by viewModel.expiringMembers.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val selectedPlanFilter by viewModel.planFilter.collectAsState()

    val checkInResult by viewModel.lastCheckInResult.collectAsState()
    val recordedPayment by viewModel.lastRecordedPayment.collectAsState()

    // Handle Android system back gestures
    BackHandler(enabled = currentScreen !is Screen.Dashboard && currentScreen !is Screen.Login) {
        viewModel.navigateBack()
    }

    if (!isLoggedIn || currentScreen is Screen.Login) {
        LoginScreen(
            onLoginSuccess = { staff ->
                viewModel.loginAs(staff)
            }
        )
    } else {
        val isSubScreen = currentScreen is Screen.MemberProfile ||
                currentScreen is Screen.AddEditMember ||
                currentScreen is Screen.RecordPayment

        val topBarTitle = when (currentScreen) {
            is Screen.Dashboard -> "FitAdmin Console"
            is Screen.MembersList -> "Members Directory"
            is Screen.MemberProfile -> "Member Profile"
            is Screen.AddEditMember -> {
                val memberId = (currentScreen as Screen.AddEditMember).memberId
                if (memberId == null) "New Member Enrollment" else "Edit Member Profile"
            }
            is Screen.PlansSetup -> "Membership Plans"
            is Screen.RecordPayment -> "Record Payment"
            is Screen.CheckIn -> "Check-In Terminal"
            is Screen.ExpiringSoon -> "Expiring Members"
            is Screen.Login -> "Login"
        }

        Scaffold(
            topBar = {
                FitAdminTopBar(
                    title = topBarTitle,
                    subtitle = if (!isSubScreen) "Facility Control Station" else null,
                    canNavigateBack = isSubScreen,
                    onNavigateBack = { viewModel.navigateBack() },
                    currentUser = currentUser,
                    onLogout = { viewModel.logout() }
                )
            },
            bottomBar = {
                // Show bottom bar for main navigation hubs
                if (!isSubScreen) {
                    FitAdminBottomBar(
                        currentScreen = currentScreen,
                        onNavigate = { screen -> viewModel.navigateTo(screen) },
                        expiringCount = expiringMembers.size
                    )
                }
            },
            containerColor = Color(0xFFF9FAFB)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Login -> {
                        LoginScreen(onLoginSuccess = { viewModel.loginAs(it) })
                    }
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            stats = dashboardStats,
                            recentCheckIns = todayCheckIns.ifEmpty { allCheckIns },
                            currentUser = currentUser,
                            onNavigate = { viewModel.navigateTo(it) },
                            onSelectMember = { memberId ->
                                viewModel.navigateTo(Screen.MemberProfile(memberId))
                            }
                        )
                    }
                    is Screen.MembersList -> {
                        MembersListScreen(
                            members = filteredMembers,
                            plans = allPlans,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.updateSearchQuery(it) },
                            statusFilter = statusFilter,
                            onStatusFilterChange = { viewModel.setStatusFilter(it) },
                            selectedPlanId = selectedPlanFilter,
                            onPlanFilterChange = { viewModel.setPlanFilter(it) },
                            onSelectMember = { memberId ->
                                viewModel.navigateTo(Screen.MemberProfile(memberId))
                            },
                            onAddNewMember = {
                                viewModel.navigateTo(Screen.AddEditMember(null))
                            }
                        )
                    }
                    is Screen.MemberProfile -> {
                        val member = allMembers.firstOrNull { it.id == screen.memberId }
                        if (member != null) {
                            val payments by viewModel.getMemberPayments(member.id).collectAsState(initial = emptyList())
                            val checkins by viewModel.getMemberCheckIns(member.id).collectAsState(initial = emptyList())

                            MemberProfileScreen(
                                member = member,
                                paymentHistory = payments,
                                checkInHistory = checkins,
                                onRenewClick = { id ->
                                    viewModel.navigateTo(Screen.RecordPayment(id))
                                },
                                onCheckInClick = { m ->
                                    viewModel.performCheckIn(m)
                                },
                                onEditClick = { id ->
                                    viewModel.navigateTo(Screen.AddEditMember(id))
                                }
                            )
                        }
                    }
                    is Screen.AddEditMember -> {
                        val existing = screen.memberId?.let { id -> allMembers.firstOrNull { it.id == id } }
                        AddEditMemberScreen(
                            existingMember = existing,
                            plans = allPlans,
                            onSave = { id, fullName, phone, email, plan, start, trainer, emergency, colorIndex ->
                                viewModel.saveMember(
                                    id, fullName, phone, email, plan, start, trainer, emergency, colorIndex
                                )
                            },
                            onDelete = { member ->
                                viewModel.deleteMember(member)
                            }
                        )
                    }
                    is Screen.PlansSetup -> {
                        PlansScreen(
                            plans = allPlans,
                            members = allMembers,
                            onSavePlan = { id, name, days, label, price, desc, perks, colorHex ->
                                viewModel.savePlan(id, name, days, label, price, desc, perks, colorHex)
                            },
                            onDeletePlan = { plan ->
                                viewModel.deletePlan(plan)
                            }
                        )
                    }
                    is Screen.RecordPayment -> {
                        RecordPaymentScreen(
                            members = allMembers,
                            plans = allPlans,
                            preselectedMemberId = screen.preselectedMemberId,
                            onSavePayment = { memberId, planId, amount, method, ref, notes ->
                                viewModel.processPayment(memberId, planId, amount, method, ref, notes)
                            }
                        )
                    }
                    is Screen.CheckIn -> {
                        CheckInScreen(
                            members = allMembers,
                            todayCheckIns = todayCheckIns,
                            onCheckInMember = { member ->
                                viewModel.performCheckIn(member)
                            }
                        )
                    }
                    is Screen.ExpiringSoon -> {
                        ExpiringSoonScreen(
                            expiringMembers = expiringMembers,
                            onSendReminder = { member ->
                                viewModel.sendReminder(member)
                            },
                            onRenewClick = { memberId ->
                                viewModel.navigateTo(Screen.RecordPayment(memberId))
                            }
                        )
                    }
                }

                // Check-in verification / alert feedback modal
                CheckInFeedbackModal(
                    result = checkInResult,
                    onDismiss = { viewModel.clearCheckInResult() },
                    onRenewMember = { memberId ->
                        viewModel.clearCheckInResult()
                        viewModel.navigateTo(Screen.RecordPayment(memberId))
                    },
                    onAdminOverride = { member ->
                        viewModel.performCheckIn(member, isOverride = true, overrideReason = "Front-Desk Exception Pass")
                    }
                )

                // Payment receipt confirmation modal
                PaymentReceiptModal(
                    payment = recordedPayment,
                    onDismiss = { viewModel.clearLastPayment() }
                )
            }
        }
    }
}
