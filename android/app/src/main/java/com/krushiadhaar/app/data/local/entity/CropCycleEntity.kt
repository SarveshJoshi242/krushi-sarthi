package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_cycles")
data class CropCycleEntity(
    @PrimaryKey val id: String,
    val fieldId: String,
    val userId: String,
    val cropName: String,
    val currentStage: String,
    val status: String
)
