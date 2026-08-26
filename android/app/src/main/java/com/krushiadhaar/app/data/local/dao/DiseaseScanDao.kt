package com.krushiadhaar.app.data.local.dao
import androidx.room.*
import com.krushiadhaar.app.data.local.entity.DiseaseScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseaseScanDao {
    @Query("SELECT * FROM disease_scans WHERE userId = :userId")
    fun getUserScans(userId: String): Flow<List<DiseaseScanEntity>>

    @Query("SELECT * FROM disease_scans WHERE id = :id AND userId = :userId")
    fun getScanById(id: String, userId: String): Flow<DiseaseScanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: DiseaseScanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScans(scans: List<DiseaseScanEntity>)

    @Query("DELETE FROM disease_scans WHERE userId = :userId")
    suspend fun deleteUserScans(userId: String)
}
