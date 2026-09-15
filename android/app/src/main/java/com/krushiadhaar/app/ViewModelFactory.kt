package com.krushiadhaar.app
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krushiadhaar.app.data.repository.AuthRepository
import com.krushiadhaar.app.data.repository.UserRepository
import com.krushiadhaar.app.presentation.MainViewModel
import com.krushiadhaar.app.presentation.auth.LoginViewModel
import com.krushiadhaar.app.presentation.auth.RegisterViewModel

class ViewModelFactory(
    private val app: KrushiAdhaarApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(app.authRepository) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(app.authRepository) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app.authRepository) as T
        }
        if (modelClass.isAssignableFrom(com.krushiadhaar.app.marketplace.MarketplaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.krushiadhaar.app.marketplace.MarketplaceViewModel(app.marketplaceRepository) as T
        }
        if (modelClass.isAssignableFrom(com.krushiadhaar.app.presentation.disease.DiseaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return com.krushiadhaar.app.presentation.disease.DiseaseViewModel(app.diseaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
