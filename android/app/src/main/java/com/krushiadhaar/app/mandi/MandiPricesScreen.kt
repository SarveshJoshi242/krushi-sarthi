package com.krushiadhaar.app.mandi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.krushiadhaar.app.util.AppStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class MandiItem(
    val commodity: String,
    val emoji: String,
    val category: String,
    val mandi: String,
    val district: String,
    val distanceKm: Int,
    val modalPrice: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val msp: Int,
    val trendPercentage: String,
    val trendDirection: String,
    val unit: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiPricesScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var mandiList by remember { mutableStateOf<List<MandiItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val fetched = withContext(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:8000/mandi-prices")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 4000
                conn.readTimeout = 5000
                if (conn.responseCode == 200) {
                    val str = conn.inputStream.bufferedReader().readText()
                    val arr = JSONArray(str)
                    val list = mutableListOf<MandiItem>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        list.add(
                            MandiItem(
                                commodity = obj.optString("commodity", "Crop"),
                                emoji = obj.optString("emoji", "🌾"),
                                category = obj.optString("category", "General"),
                                mandi = obj.optString("mandi", "APMC"),
                                district = obj.optString("district", "District"),
                                distanceKm = obj.optInt("distance_km", 25),
                                modalPrice = obj.optInt("modal_price", 3000),
                                minPrice = obj.optInt("min_price", 2800),
                                maxPrice = obj.optInt("max_price", 3200),
                                msp = obj.optInt("msp", 2500),
                                trendPercentage = obj.optString("trend_percentage", "+2.5%"),
                                trendDirection = obj.optString("trend_direction", "UP"),
                                unit = obj.optString("unit", "Quintal")
                            )
                        )
                    }
                    list
                } else {
                    getDefaultMandiList()
                }
            } catch (e: Exception) {
                getDefaultMandiList()
            }
        }
        mandiList = fetched
        isLoading = false
    }

    val categories = listOf("All", "Oilseeds", "Cereal", "Cash Crops", "Vegetables", "Pulses")

    val filteredList = mandiList.filter {
        val matchesSearch = searchQuery.isBlank() ||
            it.commodity.contains(searchQuery, ignoreCase = true) ||
            it.mandi.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(AppStrings.mandiPrices, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("📍 ${com.krushiadhaar.app.GlobalMockData.userLocation.value}", fontSize = 11.sp, color = GreenPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxSize()
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search crop or mandi (e.g. Soybean, Kolhapur)...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Category Filter Pills (All categories scrollable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) GreenPrimary else Color.White,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList) { item ->
                        MandiCard(item = item)
                    }
                }
            }
        }
    }
}
}

@Composable
private fun MandiCard(item: MandiItem) {
    val diff = item.modalPrice - item.msp
    val isAboveMsp = diff >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.commodity, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${item.mandi} (${item.distanceKm} km)", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                // Trend Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (item.trendDirection == "UP") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = "${item.trendPercentage} ${if (item.trendDirection == "UP") "📈" else "📉"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.trendDirection == "UP") Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Pricing Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Today's Modal Price", fontSize = 11.sp, color = TextSecondary)
                    Text("₹${item.modalPrice}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("per ${item.unit}", fontSize = 10.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Govt MSP Benchmark", fontSize = 11.sp, color = TextSecondary)
                    Text("₹${item.msp}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Text(
                        text = if (isAboveMsp) "+₹$diff Above MSP" else "-₹${-diff} Below MSP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAboveMsp) GreenPrimary else Color(0xFFE53935)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Range Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Min: ₹${item.minPrice}", fontSize = 11.sp, color = TextSecondary)
                Text("Max: ₹${item.maxPrice}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GreenDark)
            }
        }
    }
}

private fun getDefaultMandiList(): List<MandiItem> = listOf(
    MandiItem("Soybean", "🌱", "Oilseeds", "Kolhapur APMC", "Kolhapur", 14, 4850, 4600, 5050, 4600, "+3.4%", "UP", "Quintal"),
    MandiItem("Cotton (Medium Staple)", "🌾", "Fibre", "Nagpur APMC", "Nagpur", 42, 7250, 6900, 7500, 6620, "+1.8%", "UP", "Quintal"),
    MandiItem("Wheat (Sharbati)", "🌾", "Cereal", "Nashik APMC", "Nashik", 28, 2750, 2550, 2900, 2275, "+4.1%", "UP", "Quintal"),
    MandiItem("Sugarcane", "🎋", "Cash Crops", "Pune APMC", "Pune", 18, 3150, 2950, 3300, 3150, "0.0%", "STABLE", "Ton"),
    MandiItem("Onion (Red)", "🧅", "Vegetables", "Lasalgaon APMC", "Nashik", 65, 2200, 1800, 2600, 1900, "-2.5%", "DOWN", "Quintal"),
    MandiItem("Groundnut", "🥜", "Oilseeds", "Solapur APMC", "Solapur", 35, 6850, 6400, 7100, 6377, "+2.9%", "UP", "Quintal"),
    MandiItem("Gram (Chana)", "🫘", "Pulses", "Amravati APMC", "Amravati", 50, 5400, 5100, 5650, 5440, "-0.8%", "DOWN", "Quintal"),
    MandiItem("Rice (Basmati)", "🍚", "Cereal", "Gondia APMC", "Gondia", 72, 3800, 3400, 4200, 2183, "+1.2%", "UP", "Quintal")
)
