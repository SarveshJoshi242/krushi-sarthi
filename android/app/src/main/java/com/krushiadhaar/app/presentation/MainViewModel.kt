package com.krushiadhaar.app.presentation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiadhaar.app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val authState = authRepository.authState

    init {
        viewModelScope.launch {
            authRepository.checkSession()
        }
    }
}
