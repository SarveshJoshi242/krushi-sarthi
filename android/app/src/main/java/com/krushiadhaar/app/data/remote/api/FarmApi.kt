package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import com.krushiadhaar.app.data.local.entity.FarmEntity
import retrofit2.Response
import retrofit2.http.GET

interface FarmApi {
    @GET("api/farms")
    suspend fun getFarms(): Response<ApiResponseDto<List<FarmEntity>>>
}
