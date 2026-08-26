package com.krushiadhaar.app.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CropListing(val id: Int, val name: String, val quantity: String, val price: String, val location: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSell: () -> Unit,
    viewModel: MarketplaceViewModel
) {
    val listingsState by viewModel.listings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Crop Marketplace", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = onNavigateToSell) {
                Text("Sell Crop")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Simple filters placeholder
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterChip(selected = true, onClick = { /*TODO*/ }, label = { Text("All") })
            FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text("Nearby") })
            FilterChip(selected = false, onClick = { /*TODO*/ }, label = { Text("Price: Low-High") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(listingsState) { listing ->
                CropListingCard(listing)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Dashboard")
        }
    }
}

@Composable
fun CropListingCard(listing: com.krushiadhaar.app.data.local.entity.MarketplaceListingEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(listing.cropName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("₹${listing.pricePerUnit} / ${listing.unit}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Quantity: ${listing.availableQuantity} ${listing.unit}")
            Text("Location: ${listing.location}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO: Buying flow */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Contact Seller")
            }
        }
    }
}
