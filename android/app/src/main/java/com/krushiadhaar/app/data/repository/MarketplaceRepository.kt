package com.krushiadhaar.app.data.repository
import com.krushiadhaar.app.data.remote.api.MarketplaceApi
import com.krushiadhaar.app.data.local.dao.MarketplaceDao
import com.krushiadhaar.app.data.local.entity.MarketplaceListingEntity
import kotlinx.coroutines.flow.Flow

class MarketplaceRepository(private val api: MarketplaceApi, private val dao: MarketplaceDao) {
    suspend fun refreshListings() {
        try {
            val response = api.getListings()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { dao.insertListings(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllListings(): Flow<List<MarketplaceListingEntity>> {
        return dao.getAllListings()
    }
}
