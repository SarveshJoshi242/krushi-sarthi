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
import com.krushiadhaar.app.util.AppStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import android.widget.Toast

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
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToVoiceAssistant: () -> Unit = {},
    onNavigateToMandi: () -> Unit = {},
    onNavigateToSoilHealth: () -> Unit = {},
    onNavigateToIrrigation: () -> Unit = {},
    onNavigateToPdfReport: () -> Unit = {},
    homeViewModel: com.krushiadhaar.app.presentation.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showPlanCropSheet by remember { mutableStateOf(false) }
    var showSelectCropSheet by remember { mutableStateOf(false) }
    var selectedCropForPlan by remember { mutableStateOf<com.krushiadhaar.app.data.remote.api.CropRecommendationDto?>(null) }
    var selectedOtherCrop by remember { mutableStateOf<CropCatalogItem?>(null) }
    var cropSearchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val context = LocalContext.current
    val weatherState by homeViewModel.weatherState.collectAsState()
    var currentLang by remember { mutableStateOf(GlobalMockData.userLanguage.value) }

    val currentLat = GlobalMockData.farmerLatitude.value
    val currentLon = GlobalMockData.farmerLongitude.value
    val currentDistrict = GlobalMockData.farmerDistrict.value

    LaunchedEffect(currentLat, currentLon, currentDistrict) {
        homeViewModel.refreshWeather(currentLat, currentLon, currentDistrict)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header - 100% Responsive for All Screen Sizes & Form Factors
            val configuration = androidx.compose.ui.platform.LocalConfiguration.current
            val screenWidthDp = configuration.screenWidthDp
            val isCompactScreen = screenWidthDp < 380

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        horizontal = if (isCompactScreen) 12.dp else 16.dp,
                        vertical = if (isCompactScreen) 10.dp else 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. App Brand Logo & Title (Fixed width, never pushed or truncated)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompactScreen) 34.dp else 38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(if (isCompactScreen) 18.dp else 22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(if (isCompactScreen) 8.dp else 10.dp))
                    Text(
                        text = AppStrings.appName,
                        fontSize = if (isCompactScreen) 17.sp else 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 2. Action Cluster: Language Switcher + Community Badge + Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCompactScreen) 4.dp else 8.dp)
                ) {
                    // Language Switcher Dropdown Pill
                    val availableLangs = listOf("English", "हिंदी", "मराठी")
                    val displayLangLabel = when {
                        isCompactScreen && currentLang.startsWith("Eng", true) -> "EN"
                        isCompactScreen && currentLang.contains("हिंदी") -> "हिं"
                        isCompactScreen && currentLang.contains("मराठी") -> "मरा"
                        else -> currentLang
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = GreenSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.8f)),
                        modifier = Modifier.clickable {
                            val nextIdx = (availableLangs.indexOf(currentLang) + 1) % availableLangs.size
                            val newLang = availableLangs[if (nextIdx >= 0) nextIdx else 0]
                            currentLang = newLang
                            GlobalMockData.updateLanguage(newLang)
                            homeViewModel.refreshRecommendations(newLang, GlobalMockData.userLocation.value)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌐 $displayLangLabel",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                        }
                    }

                    // Community Button with Notification Badge
                    val unreadCommunityCount = GlobalMockData.unreadCommunityCount.intValue
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onNavigateToCommunity,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = "Community",
                                tint = GreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        if (unreadCommunityCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadCommunityCount > 9) "9+" else "$unreadCommunityCount",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Profile Button
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Welcome Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (isCompactScreen) 12.dp else 16.dp,
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val rawName = GlobalMockData.farmerName.value.trim()
                val hasName = rawName.isNotBlank()
                val displayName = if (hasName) rawName else AppStrings.farmerGreeting
                val greetingTitle = "${AppStrings.hello}, $displayName 👋"

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greetingTitle,
                        fontSize = if (isCompactScreen) 17.sp else 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Role Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GreenSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "👨‍🌾 ${AppStrings.farmerRole}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = GlobalMockData.userLocation.value,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenSurface)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${GlobalMockData.farmerCrop.value.uppercase()} • ${GlobalMockData.farmerFarmArea.value} ${GlobalMockData.farmerAreaUnit.value}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            maxLines = 1
                        )
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
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val icon = if (weatherState.condition.contains("Rain", true)) "🌧️" else if (weatherState.condition.contains("Cloud", true)) "☁️" else "☀️"
                    Text(text = icon, fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = weatherState.condition, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "Feels like", fontSize = 10.sp, color = TextSecondary)
                }
                Text(text = weatherState.temp, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Divider(modifier = Modifier.width(1.dp).height(36.dp), color = Color.LightGray.copy(alpha = 0.6f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "💧 ${weatherState.rain}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    val expectedText = if (weatherState.rain == "0 mm" || weatherState.rain == "0.0 mm") "No rain" else "Rain"
                    Text(text = expectedText, fontSize = 10.sp, color = TextSecondary)
                    Text(text = "expected", fontSize = 9.sp, color = TextSecondary)
                }
                Divider(modifier = Modifier.width(1.dp).height(36.dp), color = Color.LightGray.copy(alpha = 0.6f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌬️ ${weatherState.wind}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Wind", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Crop Status & Rotation Advisory Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val hasCrop = GlobalMockData.farmerCrop.value.isNotBlank()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasCrop) "CURRENT CROP: ${GlobalMockData.farmerCrop.value.uppercase()}" else "NO CROP SOWN YET",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (hasCrop) "Active cycle: ${GlobalMockData.farmerCrop.value} (${GlobalMockData.farmerFarmArea.value} ${GlobalMockData.farmerAreaUnit.value})" else "Choose a crop to plan your next season.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (hasCrop) "Personalized crop recommendations below compare water requirement, soil nutrients, and market yield for your next rotation." else "We'll compare crops using your farm's soil, weather, water requirement and local market demand.",
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
            Text(text = AppStrings.topCropRecommendations, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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
                onClick = {
                    selectedCropForPlan = topRec
                    showPlanCropSheet = true
                }
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
                            sowing = rec.sowingPeriod,
                            onClick = {
                                selectedCropForPlan = rec
                                showPlanCropSheet = true
                            }
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
            border = BorderStroke(1.dp, AppDivider),
            onClick = {
                cropSearchQuery = ""
                selectedCategoryFilter = "All"
                showSelectCropSheet = true
            }
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌾", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (AppStrings.isHindi()) "अन्य फसल चुनें" else if (AppStrings.isMarathi()) "इतर पीक निवडा" else "Other Crops",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (AppStrings.isHindi()) "अपनी पसंद की फसल चुनें और योजना बनाएं" else if (AppStrings.isMarathi()) "आपल्या पसंतीचे पीक निवडा व नियोजन करा" else "Choose another crop to plan or customize cycle",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Actions & Smart Features
        Text(
            text = AppStrings.farmTools,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Krishi AI Voice & Chat Assistant Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
            onClick = onNavigateToVoiceAssistant
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = AppStrings.voiceAssistant, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.25f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (AppStrings.isHindi()) "एआई सहायक" else if (AppStrings.isMarathi()) "एआय सहाय्यक" else "AI Doctor",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = if (AppStrings.isHindi()) "बोलकर या लिखकर पूछें • English, हिंदी, मराठी" else if (AppStrings.isMarathi()) "बोलून किंवा लिहून विचारा • English, हिंदी, मराठी" else "Ask questions in English, हिंदी, or मराठी",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 1: Soil & Water Management + Mandi Rates
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "🌱", title = AppStrings.soilAndWater, subtitle = "NPK & Irrigation", onClick = onNavigateToSoilHealth)
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "📈", title = AppStrings.mandiPrices, subtitle = "Live APMC Rates", onClick = onNavigateToMandi)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Buyer Portal + Inventory
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "🛒", title = "Buyer Portal", subtitle = "Negotiate & Buy", onClick = onNavigateToMarketplace)
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "📦", title = "Inventory", subtitle = "Stock & Tools", onClick = onNavigateToManagement)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: Krishi Charcha Community & Farm PDF Report
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                emoji = "👥",
                title = AppStrings.community,
                subtitle = if (AppStrings.isHindi()) "किसान मंच" else if (AppStrings.isMarathi()) "शेतकरी मंच" else "Farmer & Buyer Forum",
                onClick = onNavigateToCommunity
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                emoji = "📄",
                title = AppStrings.pdfReport,
                subtitle = "Certificate & Ledger",
                onClick = onNavigateToPdfReport
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
    
    // Bottom Sheets
    if (showPlanCropSheet) {
        val recToPlan = selectedCropForPlan ?: homeViewModel.recommendationsState.collectAsState().value.firstOrNull()
        ModalBottomSheet(
            onDismissRequest = { showPlanCropSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(recToPlan?.emoji ?: "🌱", fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (AppStrings.isHindi()) "फसल योजना: ${recToPlan?.cropName ?: "फसल"}" else if (AppStrings.isMarathi()) "पीक नियोजन: ${recToPlan?.cropName ?: "पीक"}" else "Plan ${recToPlan?.cropName ?: "Crop"}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(GreenSurface).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(recToPlan?.suitability ?: "Recommended", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                            }
                            if (!recToPlan?.netReturn.isNullOrBlank()) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFF3E0)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text("💰 ${recToPlan?.netReturn}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                }
                            }
                        }
                    }
                    IconButton(onClick = { showPlanCropSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Recommended Sowing Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenSurface),
                    border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📅", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (AppStrings.isHindi()) "अनुकूल बुवाई अवधि: ${recToPlan?.sowingPeriod ?: "अक्टूबर - नवंबर"}" else if (AppStrings.isMarathi()) "योग्य पेरणी कालावधी: ${recToPlan?.sowingPeriod ?: "ऑक्टोबर - नोव्हेंबर"}" else "Optimal Sowing: ${recToPlan?.sowingPeriod ?: "Current Season"}",
                                color = GreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        if (!recToPlan?.waterRequirement.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (AppStrings.isHindi()) "💧 जल आवश्यकता: ${recToPlan?.waterRequirement}" else if (AppStrings.isMarathi()) "💧 पाणी गरज: ${recToPlan?.waterRequirement}" else "💧 Water Requirement: ${recToPlan?.waterRequirement}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Soil Nutrients Required
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AppDivider)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🧪", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (AppStrings.isHindi()) "मृदा पोषण आवश्यकता (NPK)" else if (AppStrings.isMarathi()) "माती पोषण गरज (NPK)" else "Soil Nutrients Required (NPK)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = recToPlan?.soilNutrientsRequired ?: "Balanced NPK application according to farm soil fertility test.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Farming Technique
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AppDivider)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚜", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (AppStrings.isHindi()) "अनुशंसित कृषि पद्धति" else if (AppStrings.isMarathi()) "शिफारस केलेली कृषी पद्धती" else "Recommended Farming Technique",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = recToPlan?.farmingTechnique ?: "Line sowing with proper row spacing and moisture retention mulching.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Target Sowing Date
                Text(
                    text = if (AppStrings.isHindi()) "लक्षित बुवाई तिथि" else if (AppStrings.isMarathi()) "नियोजित पेरणी तारीख" else "Target Sowing Date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = "15 October 2026",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GreenPrimary) },
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Confirm Button
                Button(
                    onClick = {
                        val raw = recToPlan?.cropName ?: "Wheat"
                        val canonical = extractCanonicalCropName(raw)
                        GlobalMockData.setFarmerCrop(canonical)
                        homeViewModel.refreshRecommendations(GlobalMockData.userLanguage.value, GlobalMockData.userLocation.value)
                        val msg = when {
                            AppStrings.isHindi() -> "$canonical फसल की योजना बनाई गई! खेत चक्र और सलाह अपडेट हो गई है।"
                            AppStrings.isMarathi() -> "$canonical पिकाचे नियोजन झाले! शेती चक्र आणि सल्ला अपडेट केला आहे."
                            else -> "Planned $canonical! Active farm cycle and advisory updated."
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        showPlanCropSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (AppStrings.isHindi()) "पुष्टि करें और खेती शुरू करें" else if (AppStrings.isMarathi()) "निश्चित करा व लागवड सुरू करा" else "Confirm & Start Growing",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showSelectCropSheet) {
        val categories = listOf("All", "Cereals", "Pulses", "Oilseeds", "Vegetables", "Cash Crop")
        val filteredCrops = fullCropCatalog.filter { item ->
            val matchCat = selectedCategoryFilter == "All" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchQuery = cropSearchQuery.isBlank() ||
                item.name.contains(cropSearchQuery, ignoreCase = true) ||
                item.localNameHi.contains(cropSearchQuery, ignoreCase = true) ||
                item.localNameMr.contains(cropSearchQuery, ignoreCase = true) ||
                item.category.contains(cropSearchQuery, ignoreCase = true)
            matchCat && matchQuery
        }

        ModalBottomSheet(
            onDismissRequest = { showSelectCropSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showSelectCropSheet = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (AppStrings.isHindi()) "अन्य फसल चुनें" else if (AppStrings.isMarathi()) "इतर पीक निवडा" else "Select Other Crop",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    if (selectedOtherCrop != null) {
                        TextButton(onClick = { selectedOtherCrop = null }) {
                            Text(
                                text = if (AppStrings.isHindi()) "साफ़ करें" else if (AppStrings.isMarathi()) "हटवा" else "Clear",
                                color = GreenDark,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Text(
                    text = if (AppStrings.isHindi()) "अपनी पसंद की फसल चुनें और अपने खेत चक्र को कस्टमाइज़ करें।" else if (AppStrings.isMarathi()) "आपल्या पसंतीचे पीक निवडा आणि शेती चक्र सानुकूलित करा." else "Choose any crop to plan your season or customize your farm cycle.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )

                // Search Bar
                OutlinedTextField(
                    value = cropSearchQuery,
                    onValueChange = { cropSearchQuery = it },
                    placeholder = {
                        Text(
                            text = if (AppStrings.isHindi()) "फसल खोजें (गेहूं, कपास, चना, प्याज...)" else if (AppStrings.isMarathi()) "पीक शोधा (गहू, कांदा, सोयाबीन...)" else "Search crops by name, category...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenPrimary) },
                    trailingIcon = {
                        if (cropSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { cropSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = (selectedCategoryFilter == cat)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable List of Crops
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredCrops.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val customName = cropSearchQuery.trim()
                                    val customItem = CropCatalogItem(
                                        name = customName,
                                        localNameHi = customName,
                                        localNameMr = customName,
                                        emoji = "🌱",
                                        category = "Custom Crop",
                                        sowingSeason = "Flexible Season",
                                        waterReq = "3,500 m³",
                                        typicalReturn = "Variable Yield",
                                        nutrients = "Soil test recommended balanced NPK",
                                        technique = "Standard row cultivation & moisture conservation"
                                    )
                                    selectedOtherCrop = customItem
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GreenSurface),
                            border = BorderStroke(1.dp, GreenPrimary)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🌱", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Add \"$cropSearchQuery\" as Custom Crop", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenDark)
                                    Text("Tap to plan this crop for your active farm cycle", fontSize = 12.sp, color = TextSecondary)
                                }
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = GreenPrimary)
                            }
                        }
                    } else {
                        filteredCrops.forEach { item ->
                            val isSelected = (selectedOtherCrop?.name == item.name)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOtherCrop = item },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) GreenSurface else Color.White
                                ),
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) GreenPrimary else Color(0xFFEEEEEE)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else GreenSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(item.emoji, fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFE8F5E9))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(item.category, fontSize = 9.sp, color = GreenDark, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                        val localizedSub = if (AppStrings.isHindi()) item.localNameHi else if (AppStrings.isMarathi()) item.localNameMr else "${item.sowingSeason} • ${item.typicalReturn}"
                                        Text(
                                            text = localizedSub,
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "📅 ${item.sowingSeason}  |  💧 ${item.waterReq}",
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedOtherCrop = item },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = GreenPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Button
                val hasSelection = (selectedOtherCrop != null)
                Button(
                    onClick = {
                        val item = selectedOtherCrop ?: return@Button
                        val recDto = com.krushiadhaar.app.data.remote.api.CropRecommendationDto(
                            rank = "Custom",
                            emoji = item.emoji,
                            cropName = item.name,
                            suitability = "Custom Selected",
                            netReturn = item.typicalReturn,
                            waterRequirement = item.waterReq,
                            sowingPeriod = item.sowingSeason,
                            soilNutrientsRequired = item.nutrients,
                            farmingTechnique = item.technique
                        )
                        selectedCropForPlan = recDto
                        GlobalMockData.setFarmerCrop(item.name)
                        homeViewModel.refreshRecommendations(GlobalMockData.userLanguage.value, GlobalMockData.userLocation.value)
                        showSelectCropSheet = false
                        showPlanCropSheet = true
                        val msg = when {
                            AppStrings.isHindi() -> "${item.name} चुनी गई! योजना की समीक्षा करें।"
                            AppStrings.isMarathi() -> "${item.name} निवडले! नियोजनाचे पुनरावलोकन करा."
                            else -> "Selected ${item.name}! Review plan and start season below."
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    enabled = hasSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = if (hasSelection) {
                            if (AppStrings.isHindi()) "${selectedOtherCrop!!.name} की योजना बनाएं" else if (AppStrings.isMarathi()) "${selectedOtherCrop!!.name} चे नियोजन करा" else "Plan & Set ${selectedOtherCrop!!.name} as Active"
                        } else {
                            if (AppStrings.isHindi()) "कृपया एक फसल चुनें" else if (AppStrings.isMarathi()) "कृपया एक पीक निवडा" else "Select a Crop to Continue"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasSelection) Color.White else TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
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
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    lineHeight = 19.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreenSurface).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(suitability, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(GreenPrimary), contentAlignment = Alignment.Center) {
                            Text("₹", color = Color.White, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(netReturn, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(if (AppStrings.isHindi()) "शुद्ध लाभ" else if (AppStrings.isMarathi()) "निव्वळ नफा" else "Net return", fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 20.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💧", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(waterReq, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(if (AppStrings.isHindi()) "जल आवश्यकता" else if (AppStrings.isMarathi()) "पाण्याची गरज" else "Water Req.", fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 16.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(sowing, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 2, lineHeight = 13.sp, overflow = TextOverflow.Ellipsis)
                    }
                    Text(if (AppStrings.isHindi()) "बुवाई अवधि" else if (AppStrings.isMarathi()) "पेरणी काळ" else "Sowing", fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 16.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (AppStrings.isHindi()) "योजना देखें व शुरू करें →" else if (AppStrings.isMarathi()) "नियोजन पहा व लागवड करा →" else "Tap to view plan & start growing →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallRecommendationCard(
    modifier: Modifier = Modifier,
    rank: String,
    emoji: String,
    name: String,
    suitability: String,
    netReturn: String,
    waterReq: String,
    sowing: String,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
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
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    lineHeight = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreenSurface).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(suitability, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                }
                Text(
                    text = "Plan →",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(netReturn, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (AppStrings.isHindi()) "शुद्ध लाभ" else if (AppStrings.isMarathi()) "निव्वळ नफा" else "Net return", fontSize = 8.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(waterReq, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (AppStrings.isHindi()) "जल आवश्यकता" else if (AppStrings.isMarathi()) "पाण्याची गरज" else "Water Req.", fontSize = 8.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(sowing, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 2, lineHeight = 12.sp, overflow = TextOverflow.Ellipsis)
            Text(if (AppStrings.isHindi()) "बुवाई अवधि" else if (AppStrings.isMarathi()) "पेरणी काळ" else "Sowing Period", fontSize = 8.sp, color = TextSecondary)
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
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(GreenSurface), contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 13.sp,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

data class CropCatalogItem(
    val name: String,
    val localNameHi: String,
    val localNameMr: String,
    val emoji: String,
    val category: String,
    val sowingSeason: String,
    val waterReq: String,
    val typicalReturn: String,
    val nutrients: String,
    val technique: String
)

val fullCropCatalog = listOf(
    CropCatalogItem("Wheat", "गेहूं (Wheat - HD 2967)", "गहू (Wheat - समाधान)", "🌾", "Cereals", "15 Oct - 15 Nov (Rabi)", "4,200 m³", "₹48,500/ha", "N: 120, P: 60, K: 40 kg/ha", "Zero-tillage drill row sowing"),
    CropCatalogItem("Mustard", "सरसों (Mustard - Pusa 25)", "मोहरी (Mustard)", "🌱", "Oilseeds", "October - November", "2,800 m³", "₹42,000/ha", "N: 80, P: 40, S: 25 kg/ha", "Broad bed & furrow (BBF) 45cm spacing"),
    CropCatalogItem("Gram / Chickpea", "चना (Chana - JG 11)", "हरभरा (Chana - दिग्विजय)", "🫘", "Pulses", "October - December", "2,400 m³", "₹38,500/ha", "Rhizobium Bio-fertilizer, P: 50 kg/ha", "Ridge and furrow moisture conservation"),
    CropCatalogItem("Soyabean", "सोयाबीन (Soyabean - JS 335)", "सोयाबीन (Soyabean)", "🌱", "Oilseeds", "June - July (Kharif)", "3,500 m³", "₹40,000/ha", "N: 30, P: 60, K: 30 kg/ha", "Broad bed furrow (BBF) 45cm line sowing"),
    CropCatalogItem("Sugarcane", "गन्ना (Sugarcane - Co 86032)", "ऊस (Sugarcane - को 86032)", "🎋", "Cash Crop", "Oct - Nov / Jan - Feb", "18,000 m³", "₹1,20,000/ha", "N: 250, P: 100, K: 125 kg/ha", "Paired-row drip irrigation furrow planting"),
    CropCatalogItem("Cotton", "कपास (Bt Cotton)", "कापूस (Bt Cotton)", "☁️", "Cash Crop", "May - June (Kharif)", "6,500 m³", "₹55,000/ha", "N: 120, P: 60, K: 60 kg/ha", "Ridge & furrow at 90x60cm spacing"),
    CropCatalogItem("Rice / Paddy", "धान / चावल (Basmati / IR64)", "भात (Rice - इंद्रायणी)", "🌾", "Cereals", "June - July (Kharif)", "10,000 m³", "₹45,000/ha", "N: 100, P: 50, K: 50 kg/ha", "System of Rice Intensification (SRI) / Alternate wetting"),
    CropCatalogItem("Onion", "प्याज (Rabi Onion - Bhima Kiran)", "कांदा (भीमा किरण / लाल)", "🧅", "Vegetables", "October - December", "3,800 m³", "₹85,000/ha", "N: 100, P: 50, K: 50 kg/ha", "Raised nursery bed transplantation & drip lines"),
    CropCatalogItem("Tomato", "टमाटर (Hybrid Tomato)", "टोमॅटो (Tomato - अभिनव)", "🍅", "Vegetables", "Round the year / Aug - Oct", "4,000 m³", "₹90,000/ha", "N: 100, P: 60, K: 60 kg/ha", "Staking & drip fertigation with silver mulching"),
    CropCatalogItem("Potato", "आलू (Kufri Jyoti)", "बटाटा (कुफरी ज्योती)", "🥔", "Vegetables", "October - November", "3,500 m³", "₹75,000/ha", "N: 120, P: 100, K: 100 kg/ha", "Furrow ridge planting with timely earthing-up"),
    CropCatalogItem("Maize", "मक्का (Hybrid Maize)", "मका (हायब्रिड मका)", "🌽", "Cereals", "June - July / Oct - Nov", "4,500 m³", "₹36,000/ha", "N: 120, P: 60, K: 40 kg/ha", "Line sowing with 60x20cm spacing"),
    CropCatalogItem("Groundnut", "मूंगफली (Groundnut - TAG 24)", "भुईमूग (TAG 24)", "🥜", "Oilseeds", "June - July (Kharif)", "4,000 m³", "₹45,000/ha", "N: 25, P: 50, Gypsum: 400 kg/ha", "Broad bed furrow method with seed treatment"),
    CropCatalogItem("Pumpkin", "कद्दू (Pumpkin / Cucurbit)", "भोपळा (Pumpkin)", "🎃", "Vegetables", "June - July / Jan - Feb", "3,200 m³", "₹38,000/ha", "N: 60, P: 40, K: 40 kg/ha", "Ring basin pit sowing with organic manure"),
    CropCatalogItem("Sunflower", "सूरजमुखी (Sunflower - KBSH 44)", "सूर्यफूल (Sunflower)", "🌻", "Oilseeds", "June - July / Oct - Nov", "3,500 m³", "₹34,000/ha", "N: 60, P: 90, K: 40 kg/ha", "Ridge & furrow line sowing"),
    CropCatalogItem("Jowar", "ज्वार (Rabi Jowar - Maldandi)", "ज्वारी (मालदांडी M 35-1)", "🌾", "Cereals", "September - October (Rabi)", "3,000 m³", "₹32,000/ha", "N: 80, P: 40, K: 40 kg/ha", "Two-bowl seed drill deep moisture sowing"),
    CropCatalogItem("Bajra", "बाजरा (Pearl Millet)", "बाजरी (हायब्रिड बाजरी)", "🌾", "Cereals", "June - July (Kharif)", "2,500 m³", "₹26,000/ha", "N: 80, P: 40, K: 0 kg/ha", "Direct line sowing 45cm row distance"),
    CropCatalogItem("Castor seed", "अरंडी (Castor Seed - GCH 7)", "एरंडी (Castor Seed)", "🌰", "Oilseeds", "July - August", "3,000 m³", "₹36,000/ha", "N: 80, P: 40, K: 20 kg/ha", "Wide row planting 90x60cm spacing"),
    CropCatalogItem("Linseed", "अलसी (Linseed / Flaxseed)", "जवस (Linseed)", "🌾", "Oilseeds", "October - November", "2,200 m³", "₹28,000/ha", "N: 60, P: 30, K: 0 kg/ha", "Line sowing with 30cm spacing"),
    CropCatalogItem("Sesamum", "तिल (Sesame)", "तीळ (Sesame)", "🌱", "Oilseeds", "June - July", "2,000 m³", "₹30,000/ha", "N: 40, P: 25, K: 20 kg/ha", "Line sowing with fine seed sand mixture")
)

fun extractCanonicalCropName(raw: String): String {
    val lower = raw.lowercase()
    return when {
        lower.contains("wheat") || lower.contains("गेहूं") || lower.contains("गहू") -> "Wheat"
        lower.contains("mustard") || lower.contains("सरसों") || lower.contains("मोहरी") -> "Mustard"
        lower.contains("chickpea") || lower.contains("chana") || lower.contains("चना") || lower.contains("हरभरा") -> "Gram"
        lower.contains("soyabean") || lower.contains("soybean") || lower.contains("सोयाबीन") -> "Soyabean"
        lower.contains("sugarcane") || lower.contains("गन्ना") || lower.contains("ऊस") -> "Sugarcane"
        lower.contains("cotton") || lower.contains("कपास") || lower.contains("कापूस") -> "Cotton"
        lower.contains("rice") || lower.contains("paddy") || lower.contains("धान") || lower.contains("भात") -> "Rice"
        lower.contains("onion") || lower.contains("प्याज") || lower.contains("कांदा") -> "Onion"
        lower.contains("potato") || lower.contains("आलू") || lower.contains("बटाटा") -> "Potato"
        lower.contains("tomato") || lower.contains("टमाटर") || lower.contains("टोमॅटो") -> "Tomato"
        lower.contains("pumpkin") || lower.contains("कद्दू") || lower.contains("भोपळा") -> "Pumpkin"
        lower.contains("maize") || lower.contains("मक्का") || lower.contains("मका") -> "Maize"
        lower.contains("groundnut") || lower.contains("मूंगफली") || lower.contains("भुईमूग") -> "Groundnut"
        lower.contains("sunflower") || lower.contains("सूरजमुखी") || lower.contains("सूर्यफूल") -> "Sunflower"
        lower.contains("castor") || lower.contains("अरंडी") || lower.contains("एरंडी") -> "Castor seed"
        lower.contains("linseed") || lower.contains("अलसी") || lower.contains("जवस") -> "Linseed"
        lower.contains("sesamum") || lower.contains("तिल") || lower.contains("तीळ") -> "Sesamum"
        lower.contains("jowar") || lower.contains("ज्वार") || lower.contains("ज्वारी") -> "Jowar"
        lower.contains("bajra") || lower.contains("बाजरा") || lower.contains("बाजरी") -> "Bajra"
        else -> raw.split("(", "/", "-").firstOrNull()?.trim()?.ifBlank { raw } ?: raw
    }
}
