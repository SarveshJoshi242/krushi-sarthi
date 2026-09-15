package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import com.krushiadhaar.app.data.local.entity.MarketplaceListingEntity
import retrofit2.Response
import retrofit2.http.GET

interface MarketplaceApi {
    @GET("api/marketplace/listings")
    suspend fun getListings(): Response<ApiResponseDto<List<MarketplaceListingEntity>>>
}
