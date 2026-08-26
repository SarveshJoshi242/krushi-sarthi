package com.krushiadhaar.app.data.remote.api
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import com.krushiadhaar.app.data.local.entity.OrderEntity
import retrofit2.Response
import retrofit2.http.GET

interface OrderApi {
    @GET("api/marketplace/orders")
    suspend fun getOrders(): Response<ApiResponseDto<List<OrderEntity>>>
}
