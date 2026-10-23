package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberCode: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val avatarColorIndex: Int = 0,
    val photoUri: String? = null,
    val planId: Long,
    val planName: String,
    val startDate: Long,
    val expiryDate: Long,
    val assignedTrainer: String = "None",
    val emergencyContact: String? = null,
    val reminderSentCount: Int = 0,
    val lastReminderTimestamp: Long? = null
) {
    fun isMembershipActive(currentTime: Long = System.currentTimeMillis()): Boolean {
        return expiryDate >= currentTime
    }

    fun daysRemaining(currentTime: Long = System.currentTimeMillis()): Long {
        val diff = expiryDate - currentTime
        return (diff / (1000 * 60 * 60 * 24))
    }

    fun isExpiringWithin7Days(currentTime: Long = System.currentTimeMillis()): Boolean {
        val days = daysRemaining(currentTime)
        return days in 0..7
    }
}
