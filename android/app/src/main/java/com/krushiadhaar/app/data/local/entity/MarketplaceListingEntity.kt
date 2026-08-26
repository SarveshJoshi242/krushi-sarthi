package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marketplace_listings")
data class MarketplaceListingEntity(
    @PrimaryKey val id: String,
    val cropName: String,
    val pricePerUnit: Double,
    val quantityAvailable: Double,
    val unit: String
)
