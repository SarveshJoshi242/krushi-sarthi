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

object FarmRetrofitClient {
    val api: com.krushiadhaar.app.data.remote.api.FarmIntelligenceApi by lazy {
        Retrofit.Builder()
            .baseUrl(com.krushiadhaar.app.BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.krushiadhaar.app.data.remote.api.FarmIntelligenceApi::class.java)
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

    private val _recommendationsState = MutableStateFlow<List<com.krushiadhaar.app.data.remote.api.CropRecommendationDto>>(
        getDefaultRecommendations(com.krushiadhaar.app.GlobalMockData.userLanguage.value)
    )
    val recommendationsState: StateFlow<List<com.krushiadhaar.app.data.remote.api.CropRecommendationDto>> = _recommendationsState.asStateFlow()

    init {
        fetchWeather()
        fetchRecommendations()
    }

    fun getDefaultRecommendations(language: String = com.krushiadhaar.app.GlobalMockData.userLanguage.value): List<com.krushiadhaar.app.data.remote.api.CropRecommendationDto> {
        val isHi = language.contains("Hindi", true) || language.contains("हिंदी", true)
        val isMr = language.contains("Marathi", true) || language.contains("मराठी", true)

        return when {
            isHi -> listOf(
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "1",
                    emoji = "🌾",
                    cropName = "गेहूं (Wheat - HD 2967)",
                    suitability = "94% उपयुक्त",
                    netReturn = "₹48,500/हेक्टेयर",
                    waterRequirement = "4,200 m³",
                    sowingPeriod = "15 अक्टूबर - 15 नवंबर",
                    soilNutrientsRequired = "नाइट्रोजन (N): 120, फास्फोरस (P): 60, पोटाश (K): 40 किग्रा/हेक्टेयर",
                    farmingTechnique = "जीरो टिलेज सीड-कम-फर्टिलाइजर ड्रिल से कतार बुवाई"
                ),
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "2",
                    emoji = "🌱",
                    cropName = "सरसों (Pusa Mustard 25)",
                    suitability = "89% उपयुक्त",
                    netReturn = "₹42,000/हेक्टेयर",
                    waterRequirement = "2,800 m³",
                    sowingPeriod = "अक्टूबर - नवंबर",
                    soilNutrientsRequired = "नाइट्रोजन (N): 80, फास्फोरस (P): 40, सल्फर (S): 25 किग्रा/हेक्टेयर",
                    farmingTechnique = "ब्रॉड बेड एवं फरो (BBF) 45 सेमी दूरी पर बुवाई"
                ),
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "3",
                    emoji = "🫘",
                    cropName = "चना (Desi Chana - JG 11)",
                    suitability = "85% उपयुक्त",
                    netReturn = "₹38,500/हेक्टेयर",
                    waterRequirement = "2,400 m³",
                    sowingPeriod = "अक्टूबर - दिसंबर",
                    soilNutrientsRequired = "राइजोबियम कल्चर उपचार, फास्फोरस (P): 50 किग्रा/हेक्टेयर",
                    farmingTechnique = "गहरी जुताई के बाद रिज एवं फरो पद्धति"
                )
            )
            isMr -> listOf(
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "1",
                    emoji = "🌾",
                    cropName = "गहू (Wheat - समाधान/त्र्यंबक)",
                    suitability = "94% अनुकूल",
                    netReturn = "₹48,500/हेक्टर",
                    waterRequirement = "4,200 m³",
                    sowingPeriod = "15 ऑक्टोबर - 15 नोव्हेंबर",
                    soilNutrientsRequired = "नत्र: 120, स्फुरद: 60, पालाश: 40 किलो/हेक्टर",
                    farmingTechnique = "पेरणी यंत्राने 20 सेमी अंतरावर ओळ पद्धत"
                ),
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "2",
                    emoji = "🫘",
                    cropName = "हरभरा (Chana - दिग्विजय/विशाल)",
                    suitability = "89% अनुकूल",
                    netReturn = "₹42,000/हेक्टर",
                    waterRequirement = "2,400 m³",
                    sowingPeriod = "ऑक्टोबर - नोव्हेंबर",
                    soilNutrientsRequired = "रायझोबियम बीजप्रक्रिया, स्फुरद: 50 किलो/हेक्टर",
                    farmingTechnique = "रुंद सरी वरंबा (BBF) किंवा टोकण पद्धत"
                ),
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "3",
                    emoji = "🧅",
                    cropName = "कांदा / मोहरी (Rabi Onion / Mustard)",
                    suitability = "85% अनुकूल",
                    netReturn = "₹45,000/हेक्टर",
                    waterRequirement = "3,200 m³",
                    sowingPeriod = "ऑक्टोबर - डिसेंबर",
                    soilNutrientsRequired = "संतुलित एनपीके खते, सूक्ष्म अन्नद्रव्ये",
                    farmingTechnique = "गादीवाफा ठिबक सिंचन लागवड"
                )
            )
            else -> listOf(
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "1",
                    emoji = "🌾",
                    cropName = "Wheat (HD-2967 High Yield)",
                    suitability = "94% Suitable",
                    netReturn = "₹48,500/ha",
                    waterRequirement = "4,200 m³",
                    sowingPeriod = "15 Oct - 15 Nov",
                    soilNutrientsRequired = "Nitrogen (N): 120, Phosphorus (P): 60, Potash (K): 40 kg/ha",
                    farmingTechnique = "Line sowing using Zero-Tillage Seed-cum-Fertilizer Drill"
                ),
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "2",
                    emoji = "🌱",
                    cropName = "Mustard (Pusa Jaikisan)",
                    suitability = "89% Suitable",
                    netReturn = "₹42,000/ha",
                    waterRequirement = "2,800 m³",
                    sowingPeriod = "October - November",
                    soilNutrientsRequired = "Nitrogen (N): 80, Phosphorus (P): 40, Sulfur (S): 25 kg/ha",
                    farmingTechnique = "Broad Bed & Furrow (BBF) at 45 cm row spacing"
                ),
                com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                    rank = "3",
                    emoji = "🫘",
                    cropName = "Chickpea / Gram (JG-11)",
                    suitability = "85% Suitable",
                    netReturn = "₹38,500/ha",
                    waterRequirement = "2,400 m³",
                    sowingPeriod = "October - December",
                    soilNutrientsRequired = "Rhizobium Bio-fertilizer, Phosphorus (P): 50 kg/ha",
                    farmingTechnique = "Deep tilled ridge and furrow conservation method"
                )
            )
        }
    }

    fun refreshRecommendations(
        language: String = com.krushiadhaar.app.GlobalMockData.userLanguage.value,
        location: String = com.krushiadhaar.app.GlobalMockData.userLocation.value
    ) {
        // Immediate localized update so UI never blocks
        _recommendationsState.value = getDefaultRecommendations(language)

        viewModelScope.launch {
            try {
                val response = FarmRetrofitClient.api.getCropRecommendations(language = language, location = location)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()) {
                        _recommendationsState.value = body
                    }
                }
            } catch (e: Exception) {
                // Keep the smart default recommendations
                e.printStackTrace()
            }
        }
    }

    private fun fetchRecommendations() {
        refreshRecommendations()
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            try {
                val lat = com.krushiadhaar.app.GlobalMockData.farmerLatitude.value
                val lon = com.krushiadhaar.app.GlobalMockData.farmerLongitude.value
                val response = WeatherRetrofitClient.api.getCurrentWeather(lat = lat, lon = lon)
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
