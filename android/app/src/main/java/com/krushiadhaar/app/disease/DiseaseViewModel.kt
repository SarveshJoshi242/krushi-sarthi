package com.krushiadhaar.app.disease

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Models
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

sealed class DiseaseUiState {
    object Idle : DiseaseUiState()
    object Analyzing : DiseaseUiState()
    data class Success(val data: DiseaseData) : DiseaseUiState()
    data class Error(val message: String) : DiseaseUiState()
}

class DiseaseViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<DiseaseUiState>(DiseaseUiState.Idle)
    val uiState: StateFlow<DiseaseUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val mlService = MLService(application)

    fun analyzeImage(bitmap: android.graphics.Bitmap?) {
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
                val result = mlService.analyzeImage(bitmap)
                
                _progress.value = 1.0f
                delay(200) // Let UI catch up
                
                if (result != null) {
                    _uiState.value = DiseaseUiState.Success(
                        DiseaseData(
                            disease_name = result.diseaseName,
                            scientific_name = result.scientificName,
                            crop = result.crop,
                            symptoms = result.symptoms,
                            management = result.management,
                            treatment = result.treatment,
                            prevention = result.prevention,
                            severity = result.severity,
                            confidence = 0.98
                        )
                    )
                } else {
                    _uiState.value = DiseaseUiState.Error("Could not analyze image")
                }
            } catch (e: Exception) {
                _progress.value = 1.0f
                e.printStackTrace()
                _uiState.value = DiseaseUiState.Error(e.message ?: "Analysis error")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mlService.close()
    }
}
