package com.krushiadhaar.app.core.network
import com.krushiadhaar.app.core.storage.TokenStorage
import com.krushiadhaar.app.data.remote.dto.RefreshRequestDto
import com.krushiadhaar.app.data.remote.dto.AuthResponseDto
import com.krushiadhaar.app.data.remote.dto.ApiResponseDto
import com.krushiadhaar.app.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import com.google.gson.reflect.TypeToken

class TokenAuthenticator(
    private val tokenStorage: TokenStorage
) : Authenticator {

    // Use synchronous OkHttpClient for refresh to avoid dependency cycles and block efficiently
    private val refreshClient = OkHttpClient()

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            // Check if another thread already refreshed the token
            val currentToken = runBlocking { tokenStorage.getAccessToken() }
            val originalToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            if (currentToken != null && currentToken != originalToken) {
                // Token already updated by another thread
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ")
                    .build()
            }

            val refreshToken = runBlocking { tokenStorage.getRefreshToken() } ?: return null

            val refreshRequestDto = RefreshRequestDto(refreshToken)
            val requestBody = Gson().toJson(refreshRequestDto)
                .toRequestBody("application/json".toMediaType())

            val refreshRequest = Request.Builder()
                .url("api/v1/auth/refresh")
                .post(requestBody)
                .build()

            try {
                val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    val type = object : TypeToken<ApiResponseDto<AuthResponseDto>>() {}.type
                    val apiResponse: ApiResponseDto<AuthResponseDto> = Gson().fromJson(refreshResponse.body?.string(), type)
                    
                    val newTokens = apiResponse.data
                    if (newTokens != null) {
                        runBlocking { tokenStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken) }
                        
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ")
                            .build()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Refresh failed, clear session
            runBlocking { tokenStorage.clearTokens() }
            return null
        }
    }
}
