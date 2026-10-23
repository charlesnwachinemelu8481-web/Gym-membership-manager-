package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationDays: Int,
    val durationLabel: String,
    val price: Double,
    val description: String,
    val perks: String,
    val colorHex: String = "#84CC16",
    val isPopular: Boolean = false
) {
    fun perkList(): List<String> {
        return perks.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
