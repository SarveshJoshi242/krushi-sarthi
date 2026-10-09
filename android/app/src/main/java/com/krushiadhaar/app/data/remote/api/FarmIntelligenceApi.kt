package com.krushiadhaar.app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class CropRecommendationDto(
    val rank: String,
    val emoji: String,
    val cropName: String,
    val suitability: String,
    val netReturn: String,
    val waterRequirement: String,
    val sowingPeriod: String,
    val soilNutrientsRequired: String,
    val farmingTechnique: String
)

data class SchemeDto(
    val title: String,
    val provider: String,
    val description: String,
    val isActive: Boolean,
    val link: String,
    val requiredDocuments: List<String> = emptyList(),
    val type: String = "Scheme"
)

interface FarmIntelligenceApi {
    @GET("api/v1/farm-intelligence/recommendations")
    suspend fun getCropRecommendations(
        @Query("weatherCondition") weatherCondition: String = "Clear",
        @Query("currentMonth") currentMonth: String = "October",
        @Query("location") location: String = "India",
        @Query("language") language: String = "English"
    ): Response<List<CropRecommendationDto>>

    @GET("api/v1/farm-intelligence/schemes")
    suspend fun getGovernmentSchemes(
        @Query("location") location: String = "India",
        @Query("crop") crop: String = "Wheat",
        @Query("language") language: String = "English"
    ): Response<List<SchemeDto>>

    @GET("api/v1/farm-intelligence/expenses")
    suspend fun getExpenseTracker(
        @Query("crop") crop: String = "Wheat",
        @Query("area") area: String = "1 acre",
        @Query("language") language: String = "English"
    ): Response<String>
}
