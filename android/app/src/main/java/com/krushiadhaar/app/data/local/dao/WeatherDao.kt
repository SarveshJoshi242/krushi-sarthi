package com.krushiadhaar.app.data.local.dao
import androidx.room.*
import com.krushiadhaar.app.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE userId = :userId AND farmId = :farmId")
    fun getWeather(userId: String, farmId: String): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather WHERE userId = :userId")
    suspend fun deleteUserWeather(userId: String)
}
