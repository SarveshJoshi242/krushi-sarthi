package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val buyerUserId: String, // MUST isolate by user
    val status: String,
    val totalAmount: Double,
    val currency: String
)
