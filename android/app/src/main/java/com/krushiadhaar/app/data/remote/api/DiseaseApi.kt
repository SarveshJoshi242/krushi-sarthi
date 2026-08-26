package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import com.krushiadhaar.app.data.local.entity.DiseaseScanEntity
import retrofit2.Response
import retrofit2.http.GET

interface DiseaseApi {
    @GET("api/diseases/scans")
    suspend fun getDiseaseScans(): Response<ApiResponseDto<List<DiseaseScanEntity>>>
}
