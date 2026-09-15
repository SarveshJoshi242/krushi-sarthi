package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val totalArea: Double,
    val status: String
)
