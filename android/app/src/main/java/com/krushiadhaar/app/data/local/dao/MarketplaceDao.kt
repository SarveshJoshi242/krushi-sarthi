package com.krushiadhaar.app.data.local.dao
import androidx.room.*
import com.krushiadhaar.app.data.local.entity.MarketplaceListingEntity
import com.krushiadhaar.app.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {
    @Query("SELECT * FROM marketplace_listings")
    fun getListings(): Flow<List<MarketplaceListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<MarketplaceListingEntity>)

    @Query("SELECT * FROM orders WHERE buyerUserId = :userId")
    fun getMyOrders(userId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("DELETE FROM orders WHERE buyerUserId = :userId")
    suspend fun deleteUserOrders(userId: String)
}
