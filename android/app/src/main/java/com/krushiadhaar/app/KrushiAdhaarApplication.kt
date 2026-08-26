package com.krushiadhaar.app
import android.app.Application
import com.krushiadhaar.app.core.network.AuthInterceptor
import com.krushiadhaar.app.core.network.RetrofitClient
import com.krushiadhaar.app.core.network.TokenAuthenticator
import com.krushiadhaar.app.core.storage.TokenStorage
import com.krushiadhaar.app.data.remote.api.AuthApi
import com.krushiadhaar.app.data.remote.api.UserApi
import com.krushiadhaar.app.data.repository.AuthRepository
import com.krushiadhaar.app.data.repository.UserRepository

class KrushiAdhaarApplication : Application() {
    lateinit var database: com.krushiadhaar.app.data.local.database.KrushiAdhaarDatabase
    lateinit var tokenStorage: TokenStorage
    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository
    lateinit var farmRepository: com.krushiadhaar.app.data.repository.FarmRepository
    lateinit var diseaseRepository: com.krushiadhaar.app.data.repository.DiseaseRepository
    lateinit var marketplaceRepository: com.krushiadhaar.app.data.repository.MarketplaceRepository

    override fun onCreate() {
        super.onCreate()
        tokenStorage = TokenStorage(this)
        val authInterceptor = AuthInterceptor(tokenStorage)
        val tokenAuthenticator = TokenAuthenticator(tokenStorage)
        val retrofit = RetrofitClient.getClient(authInterceptor, tokenAuthenticator)
        
        database = androidx.room.Room.databaseBuilder(
            applicationContext,
            com.krushiadhaar.app.data.local.database.KrushiAdhaarDatabase::class.java,
            "krushi_adhaar_db"
        ).build()

        val authApi = retrofit.create(AuthApi::class.java)
        val userApi = retrofit.create(UserApi::class.java)
        val farmApi = retrofit.create(com.krushiadhaar.app.data.remote.api.FarmApi::class.java)
        val diseaseApi = retrofit.create(com.krushiadhaar.app.data.remote.api.DiseaseApi::class.java)
        val marketplaceApi = retrofit.create(com.krushiadhaar.app.data.remote.api.MarketplaceApi::class.java)
        
        authRepository = AuthRepository(authApi, tokenStorage, database)
        userRepository = UserRepository(userApi)
        farmRepository = com.krushiadhaar.app.data.repository.FarmRepository(farmApi, database.farmDao())
        diseaseRepository = com.krushiadhaar.app.data.repository.DiseaseRepository(diseaseApi, database.diseaseScanDao())
        marketplaceRepository = com.krushiadhaar.app.data.repository.MarketplaceRepository(marketplaceApi, database.marketplaceDao())
    }
}
