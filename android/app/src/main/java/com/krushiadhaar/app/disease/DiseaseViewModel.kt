package com.krushiadhaar.app.disease

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Models
data class DiseasePredictionResponse(
    val success: Boolean,
    val data: DiseaseData?,
    val error: String?
)

data class DiseaseData(
    val disease_name: String,
    val scientific_name: String,
    val crop: String,
    val symptoms: String,
    val management: String,
    val treatment: String,
    val prevention: String,
    val severity: String,
    val confidence: Double
)

// API Interface
interface MLServiceApi {
    @Multipart
    @POST("/predict")
    suspend fun predictDisease(
        @Part image: MultipartBody.Part
    ): DiseasePredictionResponse
}

// Retrofit Client
object MLRetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000"
    
    val api: MLServiceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MLServiceApi::class.java)
    }
}

class DiseaseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<DiseaseUiState>(DiseaseUiState.Idle)
    val uiState: StateFlow<DiseaseUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    fun analyzeImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.value = DiseaseUiState.Analyzing
            
            // Simulate progress update
            launch {
                for (i in 1..90) {
                    _progress.value = i / 100f
                    delay(30)
                }
            }

            try {
                val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)
                
                val response = MLRetrofitClient.api.predictDisease(body)
                
                _progress.value = 1.0f
                delay(200) // Let UI catch up
                
                if (response.success && response.data != null) {
                    _uiState.value = DiseaseUiState.Success(response.data)
                } else {
                    _uiState.value = DiseaseUiState.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                _progress.value = 1.0f
                e.printStackTrace()
                _uiState.value = DiseaseUiState.Error(e.message ?: "Network error")
            }
        }
    }
    
    fun reset() {
        _uiState.value = DiseaseUiState.Idle
        _progress.value = 0f
    }
}

sealed class DiseaseUiState {
    object Idle : DiseaseUiState()
    object Analyzing : DiseaseUiState()
    data class Success(val data: DiseaseData) : DiseaseUiState()
    data class Error(val message: String) : DiseaseUiState()
}
