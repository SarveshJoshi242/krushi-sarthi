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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

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
    val confidence: Double,
    val medicines: String,
    val estimatedCost: Double
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

    fun resetState() {
        _uiState.value = DiseaseUiState.Idle
        _progress.value = 0f
    }

    fun clearScannedImage() {
        com.krushiadhaar.app.GlobalMockData.capturedBitmap = null
        com.krushiadhaar.app.GlobalMockData.selectedImageUri = null
    }

    fun analyzeImage(bitmap: android.graphics.Bitmap?) {
        viewModelScope.launch {
            _uiState.value = DiseaseUiState.Analyzing
            _progress.value = 0.15f
            
            // Smooth progress animation that gracefully leads to completion
            val progressJob = launch {
                for (i in 16..95) {
                    _progress.value = i / 100f
                    delay(15) // Reaches 95% smoothly in ~1.2s
                }
            }

            try {
                val rawCrop = com.krushiadhaar.app.GlobalMockData.pestSelectedCrop.value
                val currentCrop = if (rawCrop == "Auto-Detect") null else rawCrop
                
                var diseaseData: DiseaseData? = null
                var validationFailed = false
                var validationReason = ""

                // 1. Attempt to call FastAPI ML microservice (Dual-Model + HSV Pathology)
                if (bitmap != null) {
                    try {
                        withContext(Dispatchers.IO) {
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, stream)
                            val byteArray = stream.toByteArray()

                            val bodyBuilder = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart(
                                    "file", "leaf.jpg",
                                    byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                                )

                            if (!currentCrop.isNullOrBlank()) {
                                bodyBuilder.addFormDataPart("crop_name", currentCrop)
                            }

                            val client = OkHttpClient.Builder()
                                .connectTimeout(15, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS)
                                .build()

                            // Try Android emulator loopback first, then localhost fallback
                            val urlsToTry = listOf("http://10.0.2.2:8000/predict", "http://localhost:8000/predict")
                            for (url in urlsToTry) {
                                if (diseaseData != null || validationFailed) break
                                try {
                                    val request = Request.Builder()
                                        .url(url)
                                        .post(bodyBuilder.build())
                                        .build()

                                    val response = client.newCall(request).execute()
                                    if (response.isSuccessful) {
                                        val bodyStr = response.body?.string()
                                        if (!bodyStr.isNullOrBlank()) {
                                            val json = JSONObject(bodyStr)

                                            // Check image validation gate
                                            if (json.has("is_valid_image") && !json.optBoolean("is_valid_image", true)) {
                                                validationReason = json.optString("rejection_reason", "Invalid image: Not an agricultural plant or leaf.")
                                                validationFailed = true
                                                break
                                            }

                                            val confRaw = json.optDouble("Confidence_Score", 96.5)
                                            val confDecimal = if (confRaw > 1.0) confRaw / 100.0 else confRaw
                                            
                                            diseaseData = DiseaseData(
                                                disease_name = json.optString("Disease_Name", "Unknown Disease"),
                                                scientific_name = json.optString("Scientific_Name", "Plant Pathogen"),
                                                crop = json.optString("Crop", currentCrop ?: "Crop"),
                                                symptoms = json.optString("Symptoms", ""),
                                                management = json.optString("Management", ""),
                                                treatment = json.optString("Treatment", ""),
                                                prevention = json.optString("Prevention", ""),
                                                severity = json.optString("Severity", "Moderate"),
                                                confidence = confDecimal,
                                                medicines = json.optString("Medicines", "Ridomil Gold @ 2.5g/L or Saaf @ 2g/L"),
                                                estimatedCost = json.optDouble("Estimated_Cost", 650.0)
                                            )
                                            break
                                        }
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.w("DiseaseViewModel", "ML microservice call failed for $url: ${e.message}")
                                }
                            }
                        }
                    } catch (netEx: Exception) {
                        netEx.printStackTrace()
                    }
                }

                // If cloud image validation rejected the photo, halt immediately without falling back to offline predictions
                if (validationFailed) {
                    progressJob.cancel()
                    _progress.value = 1.0f
                    _uiState.value = DiseaseUiState.Error("Photo Validation Rejected:\n$validationReason\n\nPlease point your camera directly at a crop leaf or plant.")
                    return@launch
                }

                // 2. Offline Fallback to On-Device ML Service (Instant, HSV-calibrated)
                if (diseaseData == null) {
                    val result = mlService.analyzeImage(bitmap, currentCrop)
                    if (result != null) {
                        diseaseData = DiseaseData(
                            disease_name = result.diseaseName,
                            scientific_name = result.scientificName,
                            crop = result.crop,
                            symptoms = result.symptoms,
                            management = result.management,
                            treatment = result.treatment,
                            prevention = result.prevention,
                            severity = result.severity,
                            confidence = result.confidence,
                            medicines = result.medicines,
                            estimatedCost = result.estimatedCost
                        )
                    }
                }

                progressJob.cancel()
                _progress.value = 1.0f
                delay(200) // Brief visual settling
                
                val finalResult = diseaseData
                if (finalResult != null) {
                    val cropEmoji = when {
                        finalResult.crop.contains("Pumpkin", true) -> "🎃"
                        finalResult.crop.contains("Cotton", true) -> "🪴"
                        finalResult.crop.contains("Wheat", true) -> "🌾"
                        finalResult.crop.contains("Rice", true) -> "🌾"
                        finalResult.crop.contains("Tomato", true) -> "🍅"
                        finalResult.crop.contains("Sugarcane", true) -> "🎋"
                        finalResult.crop.contains("Onion", true) -> "🧅"
                        finalResult.crop.contains("Potato", true) -> "🥔"
                        finalResult.crop.contains("Chilli", true) -> "🌶️"
                        finalResult.crop.contains("Brinjal", true) -> "🍆"
                        finalResult.crop.contains("Bitter", true) -> "🥒"
                        finalResult.crop.contains("Ridge", true) -> "🥬"
                        else -> "🌱"
                    }
                    com.krushiadhaar.app.GlobalMockData.addPestScan(
                        com.krushiadhaar.app.GlobalMockData.PestScanHistory(
                            crop = finalResult.crop,
                            diseaseName = finalResult.disease_name,
                            scientificName = finalResult.scientific_name,
                            severity = finalResult.severity,
                            confidence = finalResult.confidence,
                            symptoms = finalResult.symptoms,
                            medicines = finalResult.medicines,
                            estimatedCost = finalResult.estimatedCost,
                            management = finalResult.management,
                            dateText = "Just now",
                            emoji = cropEmoji
                        )
                    )
                    _uiState.value = DiseaseUiState.Success(finalResult)
                } else {
                    _uiState.value = DiseaseUiState.Error("Photo Validation Rejected:\nImage does not contain recognizable agricultural crop foliage.\n\nPlease upload a clear, focused photo of a plant leaf.")
                }
            } catch (e: Exception) {
                _progress.value = 1.0f
                e.printStackTrace()
                _uiState.value = DiseaseUiState.Error(e.message ?: "Analysis error")
            }
        }
    }

    fun loadHistoricalReport(scan: com.krushiadhaar.app.GlobalMockData.PestScanHistory) {
        com.krushiadhaar.app.GlobalMockData.capturedBitmap = null
        com.krushiadhaar.app.GlobalMockData.selectedImageUri = null
        _uiState.value = DiseaseUiState.Success(
            DiseaseData(
                disease_name = scan.diseaseName,
                scientific_name = scan.scientificName,
                crop = scan.crop,
                symptoms = scan.symptoms,
                management = scan.management,
                treatment = scan.management,
                prevention = "Maintain field sanitation and clean irrigation practices.",
                severity = scan.severity,
                confidence = scan.confidence,
                medicines = scan.medicines,
                estimatedCost = scan.estimatedCost
            )
        )
        _progress.value = 1.0f
    }

    override fun onCleared() {
        super.onCleared()
        mlService.close()
    }
}
