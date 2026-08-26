package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease_scans")
data class DiseaseScanEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val cropCycleId: String,
    val status: String,
    val diseaseName: String?,
    val confidence: Double?,
    val severity: String?,
    val recommendation: String?
)
