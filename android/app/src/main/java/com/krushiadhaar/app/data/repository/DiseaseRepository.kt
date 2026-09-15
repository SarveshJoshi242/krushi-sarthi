package com.krushiadhaar.app.data.repository
import com.krushiadhaar.app.data.remote.api.DiseaseApi
import com.krushiadhaar.app.data.local.dao.DiseaseScanDao
import com.krushiadhaar.app.data.local.entity.DiseaseScanEntity
import kotlinx.coroutines.flow.Flow

class DiseaseRepository(private val api: DiseaseApi, private val dao: DiseaseScanDao) {
    suspend fun refreshScans() {
        try {
            val response = api.getDiseaseScans()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { dao.insertScans(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getScanById(id: String, userId: String): Flow<DiseaseScanEntity?> {
        return dao.getScanById(id, userId)
    }
}
