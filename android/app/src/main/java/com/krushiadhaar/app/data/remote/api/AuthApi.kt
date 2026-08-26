package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponseDto<AuthResponseDto>>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiResponseDto<AuthResponseDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: RefreshRequestDto): Response<ApiResponseDto<Unit>>
}
