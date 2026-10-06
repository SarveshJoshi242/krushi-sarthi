package com.krushiadhaar.app.data.remote.api

import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

data class FinanceScheme(val id: String, val name: String, val description: String, val eligibility: String)
data class FinanceInsurance(val id: String, val provider: String, val coverage: String, val premium: String)
data class FinanceLoan(val id: String, val bankName: String, val interestRate: String, val maxAmount: Double)

interface FinanceApi {
    @GET("api/v1/finance/schemes")
    suspend fun getSchemes(): Response<ApiResponseDto<List<FinanceScheme>>>

    @GET("api/v1/finance/insurance")
    suspend fun getInsurance(): Response<ApiResponseDto<List<FinanceInsurance>>>

    @GET("api/v1/finance/loans")
    suspend fun getLoans(): Response<ApiResponseDto<List<FinanceLoan>>>
}
