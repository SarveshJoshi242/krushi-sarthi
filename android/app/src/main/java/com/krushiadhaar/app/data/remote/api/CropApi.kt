package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface CropApi {
    @GET("api/crops")
    suspend fun getCrops(): Response<ApiResponseDto<List<Any>>>
}
