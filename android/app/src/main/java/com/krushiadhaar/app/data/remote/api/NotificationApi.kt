package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface NotificationApi {
    @GET("api/notifications")
    suspend fun getNotifications(): Response<ApiResponseDto<List<Any>>>
}
