package com.krushiadhaar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDiseaseDetection: () -> Unit,
    onNavigateToManagement: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToExpense: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToMarketplace: () -> Unit = {},
    onNavigateToPitchCrop: () -> Unit = {},
    homeViewModel: com.krushiadhaar.app.presentation.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showPlanCropSheet by remember { mutableStateOf(false) }
    var showSelectCropSheet by remember { mutableStateOf(false) }
    val weatherState by homeViewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Kisaan Mitra", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
            
            IconButton(onClick = onNavigateToProfile) {
                Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = TextPrimary)
            }
        }

        // Welcome Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Hi, Sarvesh \uD83D\uDC4B", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Kolhapur, KOLHAPUR \u2022 Ma...", fontSize = 12.sp, color = TextSecondary)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "NO CROP SOWN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Weather Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8FB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val icon = if (weatherState.condition.contains("Rain", true)) "🌧️" else if (weatherState.condition.contains("Cloud", true)) "☁️" else "☀️"
                    Text(text = icon, fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = weatherState.condition, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Feels lik...", fontSize = 10.sp, color = TextSecondary)
                }
                Text(text = weatherState.temp, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Divider(modifier = Modifier.width(1.dp).height(40.dp), color = Color.LightGray)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "💧 ${weatherState.rain}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    val expectedText = if (weatherState.rain == "0 mm" || weatherState.rain == "0.0 mm") "No rain" else "Rain"
                    Text(text = expectedText, fontSize = 10.sp, color = TextSecondary)
                    Text(text = "expected", fontSize = 10.sp, color = TextSecondary)
                }
                Divider(modifier = Modifier.width(1.dp).height(40.dp), color = Color.LightGray)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌬️ ${weatherState.wind}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Wind", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // No Crop Sown Yet Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "NO CROP SOWN YET", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Choose a crop to plan your next season.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We'll compare crops using your farm's soil, weather, water requirement and local market demand.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Top Crop Recommendations
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Top Crop Recommendations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val recommendations by homeViewModel.recommendationsState.collectAsState()
        
        if (recommendations.isNotEmpty()) {
            val topRec = recommendations[0]
            RecommendationCard(
                rank = topRec.rank,
                emoji = topRec.emoji,
                name = topRec.cropName,
                suitability = topRec.suitability,
                netReturn = topRec.netReturn,
                waterReq = topRec.waterRequirement,
                sowing = topRec.sowingPeriod,
                onClick = { showPlanCropSheet = true }
            )
            
            if (recommendations.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (i in 1 until minOf(recommendations.size, 3)) {
                        val rec = recommendations[i]
                        SmallRecommendationCard(
                            modifier = Modifier.weight(1f),
                            rank = rec.rank,
                            emoji = rec.emoji,
                            name = rec.cropName,
                            suitability = rec.suitability,
                            netReturn = rec.netReturn,
                            waterReq = rec.waterRequirement,
                            sowing = rec.sowingPeriod
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "These recommendations are personalized for your farm.", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GreenDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Ranking considers: Market Demand • Water Requirement • Sowing Period", fontSize = 10.sp, color = GreenDark.copy(alpha = 0.8f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Choose another crop
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppDivider),
            onClick = { showSelectCropSheet = true }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌾", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Other", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Choose another crop", fontSize = 12.sp, color = TextSecondary)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Legacy Quick Actions
        Text(text = "Farm Tools", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83C\uDF3E", title = "Pitch Crop", subtitle = "Sell", onClick = onNavigateToPitchCrop)
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "🛒", title = "Buyer Portal", subtitle = "Marketplace", onClick = onNavigateToMarketplace)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83D\uDCE6", title = "Inventory", subtitle = "Resources", onClick = onNavigateToManagement)
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83E\uDDEE", title = "Expenses", subtitle = "Calculator", onClick = onNavigateToExpense)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83D\uDCC4", title = "Documents", subtitle = "Eligibility", onClick = onNavigateToDocuments)
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83D\uDCB0", title = "Finance", subtitle = "Schemes & Loans", onClick = onNavigateToFinance)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
    
    // Bottom Sheets
    if (showPlanCropSheet) {
        val topRec = homeViewModel.recommendationsState.collectAsState().value.firstOrNull()
        ModalBottomSheet(onDismissRequest = { showPlanCropSheet = false }) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(topRec?.emoji ?: "🌱", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Plan ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(GreenSurface).padding(12.dp)) {
                    Text("📅 Recommended Sowing: ", color = GreenDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Soil Nutrients Required", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(topRec?.soilNutrientsRequired ?: "N/A", fontSize = 12.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(12.dp))
                Text("Farming Technique", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(topRec?.farmingTechnique ?: "N/A", fontSize = 12.sp, color = TextSecondary)
                
                Spacer(modifier = Modifier.height(20.dp))
                Text("Select Sowing Date", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = "06 October 2026",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GreenPrimary) },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showPlanCropSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm & Start Growing", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showSelectCropSheet) {
        ModalBottomSheet(onDismissRequest = { showSelectCropSheet = false }) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showSelectCropSheet = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                    Text("Select Crop", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Choose a crop that isn't in your recommendations.", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                
                val crops = listOf(
                    "Groundnut" to "June - July",
                    "Castor seed" to "July - August",
                    "Sunflower" to "June - July",
                    "Sesamum" to "June - July",
                    "Linseed" to "October - November"
                )
                
                crops.forEach { (name, date) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌱", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("📅 Recommended Sowing: $date", fontSize = 12.sp, color = TextSecondary)
                        }
                        RadioButton(selected = false, onClick = {})
                    }
                    Divider(color = AppDivider)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showSelectCropSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationCard(rank: String, emoji: String, name: String, suitability: String, netReturn: String, waterReq: String, sowing: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(GreenSurface), contentAlignment = Alignment.Center) {
                    Text(rank, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreenSurface).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(suitability, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(GreenPrimary), contentAlignment = Alignment.Center) {
                            Text("₹", color = Color.White, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(netReturn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Net return", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 20.dp))
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💧", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(waterReq, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Water Req.", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 16.dp))
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(sowing, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Recommended...", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
fun SmallRecommendationCard(modifier: Modifier = Modifier, rank: String, emoji: String, name: String, suitability: String, netReturn: String, waterReq: String, sowing: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(GreenSurface), contentAlignment = Alignment.Center) {
                    Text(rank, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreenSurface).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text(suitability, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GreenDark)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(netReturn, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Net return", fontSize = 8.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(waterReq, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Water Req.", fontSize = 8.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(sowing, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Recommended Sowing", fontSize = 8.sp, color = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(modifier: Modifier = Modifier, emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(GreenSurface), contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
