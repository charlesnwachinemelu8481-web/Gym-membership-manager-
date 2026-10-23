package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val memberCode: String,
    val planId: Long,
    val planName: String,
    val amount: Double,
    val paymentDate: Long,
    val paymentMethod: String,
    val transactionRef: String,
    val extendedDays: Int,
    val notes: String? = null
)
