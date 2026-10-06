package com.krushiadhaar.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Weather API Models
data class WeatherResponse(
    val weather: List<Weather>,
    val main: MainData,
    val wind: WindData,
    val rain: RainData?
)

data class Weather(val main: String, val description: String)
data class MainData(val temp: Double, val feels_like: Double)
data class WindData(val speed: Double)
data class RainData(val `1h`: Double?)

// API Interface
interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double = 16.7050,
        @Query("lon") lon: Double = 74.2433,
        @Query("appid") appId: String = "20fe0a561f9832b040c30f5c1aaf8d6c",
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

object WeatherRetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}

data class WeatherUiState(
    val temp: String = "23°",
    val condition: String = "Clear",
    val wind: String = "4 km/h",
    val rain: String = "0 mm"
)

class HomeViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                val response = WeatherRetrofitClient.api.getCurrentWeather()
                _weatherState.value = WeatherUiState(
                    temp = "${response.main.temp.toInt()}°",
                    condition = response.weather.firstOrNull()?.main ?: "Clear",
                    wind = "${(response.wind.speed * 3.6).toInt()} km/h",
                    rain = "${response.rain?.`1h` ?: 0} mm"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Keep default if fails
            }
        }
    }
}
