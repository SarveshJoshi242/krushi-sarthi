package com.krushiadhaar.app.marketplace
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krushiadhaar.app.data.repository.MarketplaceRepository
import com.krushiadhaar.app.data.local.entity.MarketplaceListingEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {
    val listings: StateFlow<List<MarketplaceListingEntity>> = repository.getAllListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshListings()
        }
    }
}
