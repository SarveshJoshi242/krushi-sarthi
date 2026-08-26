package com.krushiadhaar.app.data.local.dao
import androidx.room.*
import com.krushiadhaar.app.data.local.entity.FarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {
    @Query("SELECT * FROM farms WHERE userId = :userId AND status = 'ACTIVE'")
    fun getActiveFarms(userId: String): Flow<List<FarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarms(farms: List<FarmEntity>)

    @Query("DELETE FROM farms WHERE userId = :userId")
    suspend fun deleteUserFarms(userId: String)
}
