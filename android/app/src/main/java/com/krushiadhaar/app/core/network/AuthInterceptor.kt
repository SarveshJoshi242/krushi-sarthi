package com.krushiadhaar.app.core.network
import com.krushiadhaar.app.core.storage.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        if (request.url.encodedPath.contains("/auth/login") || 
            request.url.encodedPath.contains("/auth/register") ||
            request.url.encodedPath.contains("/auth/refresh")) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenStorage.getAccessToken() }
        if (token != null) {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer ")
                .build()
            return chain.proceed(authenticatedRequest)
        }

        return chain.proceed(request)
    }
}
