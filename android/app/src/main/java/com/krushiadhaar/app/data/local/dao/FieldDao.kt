package com.krushiadhaar.app.data.local.dao
import androidx.room.*
import com.krushiadhaar.app.data.local.entity.FieldEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields WHERE userId = :userId AND farmId = :farmId AND status = 'ACTIVE'")
    fun getFields(userId: String, farmId: String): Flow<List<FieldEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFields(fields: List<FieldEntity>)

    @Query("DELETE FROM fields WHERE userId = :userId")
    suspend fun deleteUserFields(userId: String)
}
