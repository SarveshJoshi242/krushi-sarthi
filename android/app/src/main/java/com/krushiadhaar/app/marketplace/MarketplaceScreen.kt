package com.krushiadhaar.app.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

data class CropListing(
    val name: String,
    val farmer: String,
    val location: String,
    val quantityTons: Int,
    val pricePerTon: Int,
    val emoji: String,
    val emojiBg: Color
)

private val sampleListings = listOf(
    CropListing("Sugarcane", "Ramesh Patil", "Pune, Maharashtra", 10, 2500, "\uD83C\uDF3F", Color(0xFFE8F5E9)),
    CropListing("Cotton", "Suresh Kumar", "Nagpur, Maharashtra", 5, 8000, "\uD83C\uDF31", Color(0xFFE3F2FD))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSell: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("Sort by Price", "High Quality", "Bulk Quantity")
    var selectedFilter by remember { mutableStateOf("Sort by Price") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marketplace", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Search bar
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search for crops...", fontSize = 14.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = AppDivider,
                        focusedBorderColor = GreenPrimary
                    )
                )
            }

            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.background(Color.White)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            containerColor = Color.White,
                            selectedLabelColor = Color.White,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Active Listings",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleListings.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
                }) { listing ->
                    ListingCard(listing = listing, onClick = {})
                }
            }
        }
    }
}

@Composable
private fun ListingCard(listing: CropListing, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(listing.emojiBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = listing.emoji, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = listing.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Text(text = listing.farmer, fontSize = 12.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                        Text(text = listing.location, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = AppDivider)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Quantity", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "${listing.quantityTons} Tons", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Price", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "\u20B9${listing.pricePerTon.toFormattedString()} / Ton", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangeText)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(text = "Place Order", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

private fun Int.toFormattedString(): String {
    return if (this >= 1000) {
        val thousands = this / 1000
        val remainder = this % 1000
        if (remainder == 0) "${thousands},000" else "$thousands,${remainder.toString().padStart(3, '0')}"
    } else this.toString()
}
