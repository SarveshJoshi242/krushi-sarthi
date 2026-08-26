package com.krushiadhaar.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val farmId: String,
    val userId: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Double,
    val rainProbability: Double,
    val windSpeed: Double,
    val condition: String,
    val insights: String // Stored as comma-separated or JSON
)
