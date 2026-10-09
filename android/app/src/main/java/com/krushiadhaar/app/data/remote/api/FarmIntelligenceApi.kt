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

interface FarmIntelligenceApi {
    @GET("api/v1/farm-intelligence/recommendations")
    suspend fun getCropRecommendations(
        @Query("weatherCondition") weatherCondition: String = "Clear",
        @Query("currentMonth") currentMonth: String = "October"
    ): Response<List<CropRecommendationDto>>
}
