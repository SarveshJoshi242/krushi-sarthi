package com.krushiadhaar.app.core.network
import com.krushiadhaar.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(authInterceptor: AuthInterceptor, tokenAuthenticator: TokenAuthenticator): Retrofit {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                if (!message.contains("Bearer") && !message.contains("refresh_token") && !message.contains("password")) {
                    println("OkHttp: ")
                }
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .authenticator(tokenAuthenticator)
                .addInterceptor(loggingInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}
