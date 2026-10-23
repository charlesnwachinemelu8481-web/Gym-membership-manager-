package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_check_ins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val memberCode: String,
    val planName: String,
    val timestamp: Long,
    val status: String = "SUCCESS", // "SUCCESS", "OVERRIDDEN", "BLOCKED"
    val notes: String? = null
)
