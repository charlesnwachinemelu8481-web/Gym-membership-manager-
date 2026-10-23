package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CheckInEntity
import com.example.data.CheckInResult
import com.example.data.FitAdminDatabase
import com.example.data.FitAdminRepository
import com.example.data.MemberEntity
import com.example.data.PaymentEntity
import com.example.data.PlanEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object MembersList : Screen()
    data class MemberProfile(val memberId: Long) : Screen()
    data class AddEditMember(val memberId: Long? = null) : Screen()
    object PlansSetup : Screen()
    data class RecordPayment(val preselectedMemberId: Long? = null) : Screen()
    object CheckIn : Screen()
    object ExpiringSoon : Screen()
}

data class StaffUser(
    val name: String,
    val role: String,
    val avatarInitials: String,
    val employeeId: String
)

enum class MemberStatusFilter {
    ALL, ACTIVE, EXPIRING_SOON, EXPIRED
}

data class DashboardStats(
    val activeMembersCount: Int = 0,
    val expiringThisWeekCount: Int = 0,
    val revenueThisMonth: Double = 0.0,
    val checkInsTodayCount: Int = 0
)

class FitAdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitAdminRepository

    init {
        val database = FitAdminDatabase.getDatabase(application, viewModelScope)
        repository = FitAdminRepository(database.gymDao())
    }

    // Navigation Stack
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Current logged in Staff Admin
    private val _currentUser = MutableStateFlow(
        StaffUser(
            name = "Sarah Connor",
            role = "Front Desk Lead",
            avatarInitials = "SC",
            employeeId = "EMP-402"
        )
    )
    val currentUser: StateFlow<StaffUser> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Members Flow
    val allMembers: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Plans Flow
    val allPlans: StateFlow<List<PlanEntity>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Payments Flow
    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Check-ins Flow
    val allCheckIns: StateFlow<List<CheckInEntity>> = repository.allCheckIns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCheckIns: StateFlow<List<CheckInEntity>> = repository.getTodayCheckIns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filter States for Members List
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow(MemberStatusFilter.ALL)
    val statusFilter: StateFlow<MemberStatusFilter> = _statusFilter.asStateFlow()

    private val _planFilter = MutableStateFlow<Long?>(null)
    val planFilter: StateFlow<Long?> = _planFilter.asStateFlow()

    // Filtered Members List
    val filteredMembers: StateFlow<List<MemberEntity>> = combine(
        allMembers,
        searchQuery,
        statusFilter,
        planFilter
    ) { members, query, status, planId ->
        val now = System.currentTimeMillis()
        members.filter { member ->
            val matchesQuery = if (query.isBlank()) true else {
                member.fullName.contains(query, ignoreCase = true) ||
                        member.memberCode.contains(query, ignoreCase = true) ||
                        member.phone.contains(query, ignoreCase = true) ||
                        member.email.contains(query, ignoreCase = true)
            }

            val matchesStatus = when (status) {
                MemberStatusFilter.ALL -> true
                MemberStatusFilter.ACTIVE -> member.isMembershipActive(now)
                MemberStatusFilter.EXPIRING_SOON -> member.isExpiringWithin7Days(now)
                MemberStatusFilter.EXPIRED -> !member.isMembershipActive(now)
            }

            val matchesPlan = if (planId == null) true else member.planId == planId

            matchesQuery && matchesStatus && matchesPlan
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Expiring within 7 days members
    val expiringMembers: StateFlow<List<MemberEntity>> = allMembers.combine(_searchQuery) { members, query ->
        val now = System.currentTimeMillis()
        members.filter { it.isExpiringWithin7Days(now) || (!it.isMembershipActive(now) && it.daysRemaining(now) >= -3) }
            .sortedBy { it.expiryDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics Calculation
    val dashboardStats: StateFlow<DashboardStats> = combine(
        allMembers,
        allPayments,
        todayCheckIns
    ) { members, payments, checkins ->
        val now = System.currentTimeMillis()
        val activeCount = members.count { it.isMembershipActive(now) }
        val expiringCount = members.count { it.isExpiringWithin7Days(now) }

        // Revenue this month
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val startOfMonth = calendar.timeInMillis
        val revenue = payments.filter { it.paymentDate >= startOfMonth }.sumOf { it.amount }

        DashboardStats(
            activeMembersCount = activeCount,
            expiringThisWeekCount = expiringCount,
            revenueThisMonth = revenue,
            checkInsTodayCount = checkins.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Check-In Active Dialog / Alert State
    private val _lastCheckInResult = MutableStateFlow<CheckInResult?>(null)
    val lastCheckInResult: StateFlow<CheckInResult?> = _lastCheckInResult.asStateFlow()

    // Payment Receipt Modal State
    private val _lastRecordedPayment = MutableStateFlow<PaymentEntity?>(null)
    val lastRecordedPayment: StateFlow<PaymentEntity?> = _lastRecordedPayment.asStateFlow()

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.lastIndex)
            return true
        }
        return false
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentScreen.value = Screen.Login
        screenBackStack.clear()
    }

    fun loginAs(staff: StaffUser) {
        _currentUser.value = staff
        _isLoggedIn.value = true
        _currentScreen.value = Screen.Dashboard
        screenBackStack.clear()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: MemberStatusFilter) {
        _statusFilter.value = filter
    }

    fun setPlanFilter(planId: Long?) {
        _planFilter.value = planId
    }

    // Member Operations
    fun saveMember(
        id: Long,
        fullName: String,
        phone: String,
        email: String,
        plan: PlanEntity,
        startDate: Long,
        assignedTrainer: String,
        emergencyContact: String?,
        avatarColorIndex: Int
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val expiry = startDate + (plan.durationDays * 24L * 60L * 60L * 1000L)
            if (id == 0L) {
                // Generate next code
                val randomNum = (1000..9999).random()
                val code = "FA-$randomNum"
                val newMember = MemberEntity(
                    memberCode = code,
                    fullName = fullName.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    avatarColorIndex = avatarColorIndex,
                    planId = plan.id,
                    planName = plan.name,
                    startDate = startDate,
                    expiryDate = expiry,
                    assignedTrainer = assignedTrainer,
                    emergencyContact = emergencyContact?.trim()
                )
                val newId = repository.insertMember(newMember)
                // Record initial payment
                repository.recordPayment(
                    memberId = newId,
                    planId = plan.id,
                    amount = plan.price,
                    paymentMethod = "Credit Card",
                    transactionRef = "TXN-${UUID.randomUUID().toString().take(6).uppercase()}",
                    notes = "Initial membership enrollment"
                )
            } else {
                val existing = repository.getMemberByIdOnce(id)
                if (existing != null) {
                    val updated = existing.copy(
                        fullName = fullName.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        avatarColorIndex = avatarColorIndex,
                        assignedTrainer = assignedTrainer,
                        emergencyContact = emergencyContact?.trim()
                    )
                    repository.updateMember(updated)
                }
            }
            navigateBack()
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
            navigateBack()
        }
    }

    // Plan Operations
    fun savePlan(
        id: Long,
        name: String,
        durationDays: Int,
        durationLabel: String,
        price: Double,
        description: String,
        perks: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            val plan = PlanEntity(
                id = id,
                name = name.trim(),
                durationDays = durationDays,
                durationLabel = durationLabel.trim(),
                price = price,
                description = description.trim(),
                perks = perks.trim(),
                colorHex = colorHex
            )
            if (id == 0L) {
                repository.insertPlan(plan)
            } else {
                repository.updatePlan(plan)
            }
        }
    }

    fun deletePlan(plan: PlanEntity) {
        viewModelScope.launch {
            repository.deletePlan(plan)
        }
    }

    // Check-in Operations
    fun performCheckIn(member: MemberEntity, isOverride: Boolean = false, overrideReason: String? = null) {
        viewModelScope.launch {
            val result = repository.attemptCheckIn(member.id, isOverride, overrideReason)
            _lastCheckInResult.value = result
        }
    }

    fun clearCheckInResult() {
        _lastCheckInResult.value = null
    }

    // Payment Operations
    fun processPayment(
        memberId: Long,
        planId: Long,
        amount: Double,
        paymentMethod: String,
        transactionRef: String,
        notes: String?
    ) {
        viewModelScope.launch {
            val payment = repository.recordPayment(
                memberId = memberId,
                planId = planId,
                amount = amount,
                paymentMethod = paymentMethod,
                transactionRef = transactionRef,
                notes = notes
            )
            _lastRecordedPayment.value = payment
        }
    }

    fun clearLastPayment() {
        _lastRecordedPayment.value = null
    }

    fun sendReminder(member: MemberEntity) {
        viewModelScope.launch {
            repository.sendReminder(member.id)
        }
    }

    fun getMemberPayments(memberId: Long) = repository.getPaymentsForMember(memberId)
    fun getMemberCheckIns(memberId: Long) = repository.getCheckInsForMember(memberId)
    fun getMember(memberId: Long) = repository.getMemberById(memberId)
}
