package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {
    // --- Members ---
    @Query("SELECT * FROM gym_members ORDER BY fullName ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM gym_members WHERE id = :id")
    fun getMemberById(id: Long): Flow<MemberEntity?>

    @Query("SELECT * FROM gym_members WHERE id = :id LIMIT 1")
    suspend fun getMemberByIdOnce(id: Long): MemberEntity?

    @Query("SELECT * FROM gym_members WHERE memberCode = :code LIMIT 1")
    suspend fun getMemberByCodeOnce(code: String): MemberEntity?

    @Query("""
        SELECT * FROM gym_members 
        WHERE fullName LIKE '%' || :query || '%' 
           OR memberCode LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%'
           OR email LIKE '%' || :query || '%'
        ORDER BY fullName ASC
    """)
    fun searchMembers(query: String): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("UPDATE gym_members SET expiryDate = :newExpiry, planId = :planId, planName = :planName WHERE id = :id")
    suspend fun updateMemberExpiry(id: Long, newExpiry: Long, planId: Long, planName: String)

    @Query("UPDATE gym_members SET reminderSentCount = reminderSentCount + 1, lastReminderTimestamp = :timestamp WHERE id = :id")
    suspend fun markReminderSent(id: Long, timestamp: Long)

    // --- Plans ---
    @Query("SELECT * FROM gym_plans ORDER BY price ASC")
    fun getAllPlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM gym_plans WHERE id = :id LIMIT 1")
    suspend fun getPlanByIdOnce(id: Long): PlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlans(plans: List<PlanEntity>)

    @Update
    suspend fun updatePlan(plan: PlanEntity)

    @Delete
    suspend fun deletePlan(plan: PlanEntity)

    // --- Payments ---
    @Query("SELECT * FROM gym_payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM gym_payments WHERE memberId = :memberId ORDER BY paymentDate DESC")
    fun getPaymentsForMember(memberId: Long): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(payments: List<PaymentEntity>)

    // --- Check-Ins ---
    @Query("SELECT * FROM gym_check_ins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM gym_check_ins WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getCheckInsForMember(memberId: Long): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM gym_check_ins WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getTodayCheckIns(startOfDay: Long): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCheckIns(checkIns: List<CheckInEntity>)
}
