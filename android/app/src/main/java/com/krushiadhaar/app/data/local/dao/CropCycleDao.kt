package com.krushiadhaar.app.data.local.dao
import androidx.room.*
import com.krushiadhaar.app.data.local.entity.CropCycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CropCycleDao {
    @Query("SELECT * FROM crop_cycles WHERE userId = :userId AND fieldId = :fieldId AND status = 'ACTIVE'")
    fun getCropCycles(userId: String, fieldId: String): Flow<List<CropCycleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCropCycles(cycles: List<CropCycleEntity>)

    @Query("DELETE FROM crop_cycles WHERE userId = :userId")
    suspend fun deleteUserCropCycles(userId: String)
}
