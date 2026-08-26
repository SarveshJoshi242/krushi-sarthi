package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface InventoryApi {
    @GET("api/inventory")
    suspend fun getInventory(): Response<ApiResponseDto<List<Any>>>
}
