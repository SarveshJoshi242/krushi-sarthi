package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<ApiResponseDto<UserResponseDto>>
}
