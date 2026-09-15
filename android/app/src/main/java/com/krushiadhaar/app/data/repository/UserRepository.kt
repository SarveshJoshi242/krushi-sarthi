package com.krushiadhaar.app.data.repository
import com.krushiadhaar.app.data.remote.api.UserApi
import com.krushiadhaar.app.data.remote.dto.*
import retrofit2.Response

class UserRepository(private val userApi: UserApi) {
    suspend fun getCurrentUser(): Result<UserResponseDto> {
        return try {
            val response = userApi.getCurrentUser()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown Error"))
                }
            } else {
                Result.failure(Exception("HTTP "))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
