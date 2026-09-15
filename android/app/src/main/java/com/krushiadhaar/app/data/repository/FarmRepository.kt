package com.krushiadhaar.app.data.repository
import com.krushiadhaar.app.data.remote.api.FarmApi
import com.krushiadhaar.app.data.local.dao.FarmDao
import com.krushiadhaar.app.data.local.entity.FarmEntity

class FarmRepository(private val api: FarmApi, private val dao: FarmDao) {
    suspend fun refreshFarms() {
        try {
            val response = api.getFarms()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { dao.insertFarms(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
