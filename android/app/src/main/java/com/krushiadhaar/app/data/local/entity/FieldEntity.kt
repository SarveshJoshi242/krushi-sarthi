package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val userId: String,
    val name: String,
    val area: Double,
    val status: String
)
