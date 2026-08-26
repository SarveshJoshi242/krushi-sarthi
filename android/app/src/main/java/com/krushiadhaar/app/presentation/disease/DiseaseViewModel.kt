package com.krushiadhaar.app.presentation.disease
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiadhaar.app.data.repository.DiseaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiseaseViewModel(private val repository: DiseaseRepository) : ViewModel() {
    private val _scanStatus = MutableStateFlow<String>("IDLE")
    val scanStatus: StateFlow<String> = _scanStatus

    fun startPolling(scanId: String, userId: String) {
        viewModelScope.launch {
            repository.refreshScans()
            repository.getScanById(scanId, userId).collect { scan ->
                if (scan != null) {
                    _scanStatus.value = scan.status
                }
            }
        }
    }
}
