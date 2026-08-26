package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface PaymentApi {
    @GET("api/payments")
    suspend fun getPayments(): Response<ApiResponseDto<List<Any>>>
}
