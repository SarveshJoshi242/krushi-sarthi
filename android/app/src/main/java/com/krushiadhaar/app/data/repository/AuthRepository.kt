package com.krushiadhaar.app.data.repository
import com.krushiadhaar.app.core.storage.TokenStorage
import com.krushiadhaar.app.data.remote.api.AuthApi
import com.krushiadhaar.app.data.remote.dto.*
import com.krushiadhaar.app.domain.model.AuthenticationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val database: com.krushiadhaar.app.data.local.database.KrushiAdhaarDatabase
) {
    private val _authState = MutableStateFlow<AuthenticationState>(AuthenticationState.Loading)
    val authState: StateFlow<AuthenticationState> = _authState.asStateFlow()

    suspend fun checkSession() {
        val token = tokenStorage.getAccessToken()
        if (token != null) {
            _authState.value = AuthenticationState.Authenticated
        } else {
            _authState.value = AuthenticationState.Unauthenticated
        }
    }

    suspend fun login(request: LoginRequestDto): Result<AuthResponseDto> {
        return safeApiCall { authApi.login(request) }.onSuccess { 
            tokenStorage.saveTokens(it.accessToken, it.refreshToken)
            _authState.value = AuthenticationState.Authenticated
        }
    }

    suspend fun register(request: RegisterRequestDto): Result<AuthResponseDto> {
        return safeApiCall { authApi.register(request) }.onSuccess {
            tokenStorage.saveTokens(it.accessToken, it.refreshToken)
            _authState.value = AuthenticationState.Authenticated
        }
    }

    suspend fun logout() {
        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken != null) {
            try {
                authApi.logout(RefreshRequestDto(refreshToken))
            } catch (e: Exception) {
                // Ignore network errors on logout, we still want to clear local data
            }
        }
        val token = tokenStorage.getAccessToken()
        val userId = token?.let { extractUserId(it) }

        tokenStorage.clearTokens()
        
        if (userId != null) {
            database.farmDao().deleteUserFarms(userId)
            database.fieldDao().deleteUserFields(userId)
            database.cropCycleDao().deleteUserCropCycles(userId)
            database.diseaseScanDao().deleteUserScans(userId)
            database.marketplaceDao().deleteUserOrders(userId)
        }
        
        _authState.value = AuthenticationState.Unauthenticated
    }

    private fun extractUserId(token: String): String? {
        try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                val jsonObject = org.json.JSONObject(payload)
                return jsonObject.optString("sub")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<ApiResponseDto<T>>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.error?.message ?: "Unknown API Error"))
                }
            } else {
                Result.failure(Exception("HTTP "))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
