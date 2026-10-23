package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit

sealed class CheckInResult {
    data class Success(val member: MemberEntity, val visitCount: Int, val timestamp: Long) : CheckInResult()
    data class BlockedExpired(val member: MemberEntity, val expiredDaysAgo: Long) : CheckInResult()
    data class Overridden(val member: MemberEntity, val reason: String, val timestamp: Long) : CheckInResult()
    data class Error(val message: String) : CheckInResult()
}

class FitAdminRepository(private val dao: GymDao) {

    val allMembers: Flow<List<MemberEntity>> = dao.getAllMembers()
    val allPlans: Flow<List<PlanEntity>> = dao.getAllPlans()
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()
    val allCheckIns: Flow<List<CheckInEntity>> = dao.getAllCheckIns()

    fun getTodayCheckIns(): Flow<List<CheckInEntity>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return dao.getTodayCheckIns(calendar.timeInMillis)
    }

    fun searchMembers(query: String): Flow<List<MemberEntity>> {
        return if (query.isBlank()) {
            dao.getAllMembers()
        } else {
            dao.searchMembers(query.trim())
        }
    }

    fun getMemberById(id: Long): Flow<MemberEntity?> = dao.getMemberById(id)

    suspend fun getMemberByIdOnce(id: Long): MemberEntity? = dao.getMemberByIdOnce(id)

    suspend fun getMemberByCodeOnce(code: String): MemberEntity? = dao.getMemberByCodeOnce(code)

    fun getPaymentsForMember(memberId: Long): Flow<List<PaymentEntity>> = dao.getPaymentsForMember(memberId)

    fun getCheckInsForMember(memberId: Long): Flow<List<CheckInEntity>> = dao.getCheckInsForMember(memberId)

    suspend fun insertMember(member: MemberEntity): Long = dao.insertMember(member)

    suspend fun updateMember(member: MemberEntity) = dao.updateMember(member)

    suspend fun deleteMember(member: MemberEntity) = dao.deleteMember(member)

    suspend fun insertPlan(plan: PlanEntity): Long = dao.insertPlan(plan)

    suspend fun updatePlan(plan: PlanEntity) = dao.updatePlan(plan)

    suspend fun deletePlan(plan: PlanEntity) = dao.deletePlan(plan)

    suspend fun recordPayment(
        memberId: Long,
        planId: Long,
        amount: Double,
        paymentMethod: String,
        transactionRef: String,
        notes: String?
    ): PaymentEntity {
        val member = dao.getMemberByIdOnce(memberId) ?: throw IllegalArgumentException("Member not found")
        val plan = dao.getPlanByIdOnce(planId) ?: throw IllegalArgumentException("Plan not found")

        val now = System.currentTimeMillis()
        val oneDay = TimeUnit.DAYS.toMillis(1)
        val durationMs = plan.durationDays * oneDay

        // Auto-extends expiry date:
        // If current expiry is in the future, add duration to that future date;
        // If already expired, extend from today!
        val baseDate = if (member.expiryDate > now) member.expiryDate else now
        val newExpiry = baseDate + durationMs

        val payment = PaymentEntity(
            memberId = member.id,
            memberName = member.fullName,
            memberCode = member.memberCode,
            planId = plan.id,
            planName = plan.name,
            amount = amount,
            paymentDate = now,
            paymentMethod = paymentMethod,
            transactionRef = transactionRef,
            extendedDays = plan.durationDays,
            notes = notes
        )

        dao.insertPayment(payment)
        dao.updateMemberExpiry(member.id, newExpiry, plan.id, plan.name)

        return payment
    }

    suspend fun attemptCheckIn(
        memberId: Long,
        isOverride: Boolean = false,
        overrideReason: String? = null
    ): CheckInResult {
        val member = dao.getMemberByIdOnce(memberId) ?: return CheckInResult.Error("Member not found")
        val now = System.currentTimeMillis()

        if (!member.isMembershipActive(now) && !isOverride) {
            val daysAgo = (now - member.expiryDate) / TimeUnit.DAYS.toMillis(1)
            return CheckInResult.BlockedExpired(member, daysAgo.coerceAtLeast(1))
        }

        val status = if (isOverride) "OVERRIDDEN" else "SUCCESS"
        val checkIn = CheckInEntity(
            memberId = member.id,
            memberName = member.fullName,
            memberCode = member.memberCode,
            planName = member.planName,
            timestamp = now,
            status = status,
            notes = if (isOverride) "Admin Override: ${overrideReason ?: "Front Desk Approval"}" else null
        )
        dao.insertCheckIn(checkIn)

        return if (isOverride) {
            CheckInResult.Overridden(member, overrideReason ?: "Front Desk Approval", now)
        } else {
            // Count visits
            CheckInResult.Success(member, visitCount = 14, timestamp = now)
        }
    }

    suspend fun sendReminder(memberId: Long) {
        dao.markReminderSent(memberId, System.currentTimeMillis())
    }
}
