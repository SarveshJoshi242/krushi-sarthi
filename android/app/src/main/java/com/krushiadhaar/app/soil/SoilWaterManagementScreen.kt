package com.krushiadhaar.app.soil

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.*
import kotlin.math.roundToInt

data class CropRdf(
    val n: Double,
    val p: Double,
    val k: Double,
    val isLegume: Boolean = false,
    val splitRatio: Triple<Double, Double, Double> = Triple(0.33, 0.33, 0.34)
)

fun getCropRdf(crop: String): CropRdf {
    return when (crop.trim().lowercase()) {
        "wheat" -> CropRdf(48.0, 24.0, 16.0, false, Triple(0.33, 0.33, 0.34))
        "sugarcane" -> CropRdf(100.0, 40.0, 50.0, false, Triple(0.30, 0.40, 0.30))
        "cotton" -> CropRdf(48.0, 24.0, 24.0, false, Triple(0.25, 0.50, 0.25))
        "soybean", "soyabean" -> CropRdf(12.0, 30.0, 12.0, true, Triple(1.0, 0.0, 0.0))
        "rice", "paddy" -> CropRdf(40.0, 20.0, 20.0, false, Triple(0.50, 0.25, 0.25))
        "maize" -> CropRdf(48.0, 24.0, 20.0, false, Triple(0.33, 0.33, 0.34))
        "onion" -> CropRdf(40.0, 20.0, 20.0, false, Triple(0.50, 0.25, 0.25))
        "potato" -> CropRdf(60.0, 40.0, 40.0, false, Triple(0.50, 0.50, 0.0))
        "tomato" -> CropRdf(48.0, 24.0, 24.0, false, Triple(0.33, 0.33, 0.34))
        "pumpkin" -> CropRdf(32.0, 20.0, 20.0, false, Triple(0.33, 0.33, 0.34))
        "groundnut" -> CropRdf(10.0, 25.0, 15.0, true, Triple(1.0, 0.0, 0.0))
        "gram", "chickpea" -> CropRdf(10.0, 25.0, 10.0, true, Triple(1.0, 0.0, 0.0))
        "chilli" -> CropRdf(48.0, 24.0, 24.0, false, Triple(0.33, 0.33, 0.34))
        "mustard" -> CropRdf(32.0, 16.0, 16.0, false, Triple(0.50, 0.50, 0.0))
        else -> CropRdf(40.0, 20.0, 20.0, false, Triple(0.33, 0.33, 0.34))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilWaterManagementScreen(
    onNavigateBack: () -> Unit,
    initialTab: Int = 0
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val currentLang = GlobalMockData.userLanguage.value

    val isMr = currentLang.contains("Marathi", true) || currentLang.contains("मराठी", true)
    val isHi = currentLang.contains("Hindi", true) || currentLang.contains("हिंदी", true)

    // Shared Farm Profile State
    var selectedCrop by remember {
        mutableStateOf(GlobalMockData.farmerCrop.value.ifBlank { "Wheat" })
    }
    var acreage by remember {
        mutableStateOf(GlobalMockData.farmerFarmArea.value.ifBlank { "5.0" })
    }

    // Soil Health Parameters State
    var nitrogen by remember { mutableFloatStateOf(210f) }      // kg/ha
    var phosphorus by remember { mutableFloatStateOf(18f) }     // kg/ha
    var potassium by remember { mutableFloatStateOf(220f) }     // kg/ha
    var soilPh by remember { mutableFloatStateOf(7.2f) }

    // Irrigation Parameters State
    var sowingDays by remember { mutableIntStateOf(28) }
    var soilMoisture by remember { mutableFloatStateOf(38f) }   // % moisture

    val crops = listOf(
        "Wheat", "Soybean", "Cotton", "Sugarcane", "Rice", 
        "Maize", "Onion", "Potato", "Tomato", "Pumpkin", 
        "Groundnut", "Gram", "Chilli", "Mustard"
    )

    val tabTitles = remember(currentLang) {
        if (isMr) listOf("🧪 माती आरोग्य (NPK खत गणना)", "💧 टप्प्याटप्प्याने स्मार्ट सिंचन")
        else if (isHi) listOf("🧪 मृदा स्वास्थ्य (NPK उर्वरक गणना)", "💧 चरणबद्ध स्मार्ट सिंचाई")
        else listOf("🧪 Soil Health & NPK", "💧 Step-by-Step Irrigation")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isMr) "माती व पाणी व्यवस्थापन"
                            else if (isHi) "मृदा व जल प्रबंधन"
                            else "Soil & Water Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (selectedTab == 0) "Dynamic NPK Diagnostics & 50kg Bags" else "Simple 4-Step Smart Irrigation Guide",
                            fontSize = 11.sp,
                            color = GreenPrimary
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Header
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenPrimary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) GreenPrimary else TextSecondary
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 680.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Shared Crop & Acreage Selector Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Eco, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isMr) "शेताची माहिती (पीक व क्षेत्र)"
                                        else if (isHi) "सक्रिय खेत एवं फसल विवरण"
                                        else "Active Field Setup",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GreenSurface
                                ) {
                                    Text(
                                        text = "$selectedCrop • $acreage Acres",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isMr) "पीक निवडा (Select Crop):" else if (isHi) "फसल चुनें (Select Crop):" else "Select Crop:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Horizontally scrollable row with all crops
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                crops.forEach { crop ->
                                    val isSelected = selectedCrop.equals(crop, true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCrop = crop
                                            GlobalMockData.farmerCrop.value = crop
                                        },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = GreenDark) }
                                        } else null,
                                        label = { Text(crop, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GreenSurface,
                                            selectedLabelColor = GreenDark
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Acreage Controls
                            Text(
                                text = if (isMr) "शेतीचे क्षेत्र (Area in Acres):" else if (isHi) "खेत का रकबा (Area in Acres):" else "Cultivation Area (Acres):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = acreage,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() || it == '.' }) {
                                        acreage = input
                                        GlobalMockData.farmerFarmArea.value = input
                                    }
                                },
                                label = { Text("Acres") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = GreenPrimary)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick acre chips in scrollable row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("1.0", "2.0", "5.0", "10.0").forEach { quickAcre ->
                                    FilterChip(
                                        selected = acreage == quickAcre,
                                        onClick = {
                                            acreage = quickAcre
                                            GlobalMockData.farmerFarmArea.value = quickAcre
                                        },
                                        label = { Text("${quickAcre.substringBefore(".")} Ac", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GreenSurface,
                                            selectedLabelColor = GreenDark
                                        )
                                    )
                                }
                            }
                        }
                    }

                if (selectedTab == 0) {
                    SoilHealthTabContent(
                        selectedCrop = selectedCrop,
                        acreage = acreage.toDoubleOrNull() ?: 1.0,
                        nitrogen = nitrogen,
                        phosphorus = phosphorus,
                        potassium = potassium,
                        soilPh = soilPh,
                        onNitrogenChange = { nitrogen = it },
                        onPhosphorusChange = { phosphorus = it },
                        onPotassiumChange = { potassium = it },
                        onSoilPhChange = { soilPh = it },
                        isMr = isMr,
                        isHi = isHi
                    )
                } else {
                    SmartIrrigationTabContent(
                        selectedCrop = selectedCrop,
                        acreage = acreage.toDoubleOrNull() ?: 1.0,
                        sowingDays = sowingDays,
                        onSowingDaysChange = { sowingDays = it },
                        soilMoisture = soilMoisture,
                        onSoilMoistureChange = { soilMoisture = it },
                        isMr = isMr,
                        isHi = isHi
                    )
                }
            }
        }
    }
}
}

@Composable
private fun SoilHealthTabContent(
    selectedCrop: String,
    acreage: Double,
    nitrogen: Float,
    phosphorus: Float,
    potassium: Float,
    soilPh: Float,
    onNitrogenChange: (Float) -> Unit,
    onPhosphorusChange: (Float) -> Unit,
    onPotassiumChange: (Float) -> Unit,
    onSoilPhChange: (Float) -> Unit,
    isMr: Boolean,
    isHi: Boolean
) {
    val rdf = getCropRdf(selectedCrop)

    // Soil test status evaluation
    val nStatus = if (nitrogen < 280) "Low (Deficient)" else if (nitrogen <= 560) "Medium (Optimal)" else "High (Excess)"
    val pStatus = if (phosphorus < 10) "Low (Deficient)" else if (phosphorus <= 25) "Medium (Optimal)" else "High (Excess)"
    val kStatus = if (potassium < 110) "Low (Deficient)" else if (potassium <= 280) "Medium (Optimal)" else "High (Excess)"

    val factorN = if (nitrogen < 280) 1.30 else if (nitrogen <= 560) 1.00 else 0.70
    val factorP = if (phosphorus < 10) 1.30 else if (phosphorus <= 25) 1.00 else 0.70
    val factorK = if (potassium < 110) 1.30 else if (potassium <= 280) 1.00 else 0.70

    val effectiveAcreage = if (acreage > 0) acreage else 1.0
    val reqN = rdf.n * factorN * effectiveAcreage
    val reqP = rdf.p * factorP * effectiveAcreage
    val reqK = rdf.k * factorK * effectiveAcreage

    // 50kg DAP bag (18:46:0) has 9kg N and 23kg P2O5
    val dapBags = Math.round((reqP / 23.0) * 10.0) / 10.0
    val nFromDap = dapBags * 9.0

    // Remaining N satisfied by Urea (46% N -> 23kg N per 50kg bag)
    val remainingN = maxOf(0.0, reqN - nFromDap)
    val ureaBags = Math.round((remainingN / 23.0) * 10.0) / 10.0

    // MOP (60% K2O -> 30kg K2O per 50kg bag)
    val mopBags = Math.round((reqK / 30.0) * 10.0) / 10.0

    // pH Advice
    val (phStatus, phAdvice, phColor) = when {
        soilPh < 6.0f -> Triple(
            "Acidic (pH < 6.0)",
            if (isMr) "माती आम्लयुक्त आहे. आम्लता कमी करण्यासाठी २५० किलो/एकर कृषी चुना (कॅल्शियम कार्बोनेट) मिसळा."
            else if (isHi) "मिट्टी अम्लीय है। अम्लीयता दूर करने के लिए 250 किग्रा/एकड़ कृषि चूना (कैल्शियम कार्बोनेट) डालें।"
            else "Apply Agricultural Lime (Calcium Carbonate) @ 250 kg/acre to neutralize soil acidity and boost nutrient uptake.",
            Color(0xFFE53935)
        )
        soilPh > 8.0f -> Triple(
            "Alkaline / Saline (pH > 8.0)",
            if (isMr) "माती क्षारयुक्त आहे. क्षारता कमी करण्यासाठी २०० किलो/एकर जिप्सम आणि शेणखत मिसळा."
            else if (isHi) "मिट्टी क्षारीय है। क्षारीयता कम करने के लिए 200 किग्रा/एकड़ जिप्सम और कम्पोस्ट खाद का प्रयोग करें।"
            else "Apply Gypsum @ 200 kg/acre with organic compost or green manure to reduce exchangeable sodium.",
            Color(0xFFFF9800)
        )
        else -> Triple(
            "Optimal / Neutral (6.0 - 7.5)",
            if (isMr) "मातीचा सामू (pH) उत्तम आहे. सूक्ष्मजीवांची वाढ आणि अन्नद्रव्यांचे शोषण परिपूर्ण होईल."
            else if (isHi) "मिट्टी का पीएच संतुलित है। यह फसलों के पोषक तत्वों और जीवाणुओं के लिए सर्वोत्तम है।"
            else "Soil pH is balanced. Excellent biological microbial activity and ideal for healthy root uptake.",
            GreenPrimary
        )
    }

    // 1. Soil Health Laboratory Parameters Card
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isMr) "प्रयोगशाळा माती मूल्ये (Soil Test)"
                    else if (isHi) "प्रयोगशाला मृदा परीक्षण (Soil Test)"
                    else "Soil Test Parameters (Lab Values)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Slide to adjust",
                    fontSize = 11.sp,
                    color = GreenPrimary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Nitrogen
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Available Nitrogen (N): ${nitrogen.roundToInt()} kg/ha", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                NutrientStatusBadge(nStatus)
            }
            Slider(
                value = nitrogen,
                onValueChange = onNitrogenChange,
                valueRange = 100f..650f,
                colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary)
            )

            // Phosphorus
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Available Phosphorus (P): ${phosphorus.roundToInt()} kg/ha", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                NutrientStatusBadge(pStatus)
            }
            Slider(
                value = phosphorus,
                onValueChange = onPhosphorusChange,
                valueRange = 5f..60f,
                colors = SliderDefaults.colors(thumbColor = OrangePrimary, activeTrackColor = OrangePrimary)
            )

            // Potassium
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Available Potassium (K): ${potassium.roundToInt()} kg/ha", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                NutrientStatusBadge(kStatus)
            }
            Slider(
                value = potassium,
                onValueChange = onPotassiumChange,
                valueRange = 50f..450f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF1976D2), activeTrackColor = Color(0xFF1976D2))
            )

            // Soil pH
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Soil Reaction (pH): ${String.format(java.util.Locale.US, "%.1f", soilPh)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(phStatus, color = phColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Slider(
                value = soilPh,
                onValueChange = onSoilPhChange,
                valueRange = 4.0f..9.5f,
                colors = SliderDefaults.colors(thumbColor = phColor, activeTrackColor = phColor)
            )
        }
    }

    // 2. pH Soil Amendment Advice
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = phColor.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, phColor.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = phColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isMr) "माती सुधारणा सल्ला (pH Advisory)" else if (isHi) "मृदा सुधार सलाह (pH Advisory)" else "Soil Amendment Advice",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = phColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(phAdvice, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
            }
        }
    }

    // 3. Recommended Commercial 50kg Fertilizer Bags Card
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = if (isMr) "आवश्यक खत गोणी (५० किलो बॅग)"
                        else if (isHi) "अनुशंसित उर्वरक बोरी (50 किग्रा बैग)"
                        else "Prescribed 50 kg Fertilizer Bags",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Crop: $selectedCrop | Area: $effectiveAcreage Acres",
                        fontSize = 11.sp,
                        color = GreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FertilizerBagItem(
                    modifier = Modifier.weight(1f),
                    title = "Urea (46% N)",
                    bags = ureaBags,
                    color = GreenPrimary,
                    emoji = "⚪"
                )
                FertilizerBagItem(
                    modifier = Modifier.weight(1f),
                    title = "DAP (18:46:0)",
                    bags = dapBags,
                    color = OrangePrimary,
                    emoji = "🟤"
                )
                FertilizerBagItem(
                    modifier = Modifier.weight(1f),
                    title = "MOP (60% K)",
                    bags = mopBags,
                    color = Color(0xFF1976D2),
                    emoji = "🔴"
                )
            }
        }
    }

    // 4. 3-Step Scientific Application Schedule
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isMr) "३ टप्प्यांचे वैज्ञानिक खत वेळापत्रक"
                else if (isHi) "3 चरणों का वैज्ञानिक उर्वरक कैलेंडर"
                else "3-Step Scientific Fertilizer Schedule",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = "Tailored for $selectedCrop based on active soil test values",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (rdf.isLegume) {
                ScheduleStageRow(
                    stageNumber = "1",
                    stageName = if (isMr) "पेरणी वेळी (Basal Application)" else if (isHi) "बुवाई के समय (बेसल ड्रेसिंग)" else "Basal Dressing (At Sowing)",
                    instruction = if (isMr) "१००% DAP ($dapBags गोणी) + १००% MOP ($mopBags गोणी) + १००% युरिया ($ureaBags गोणी) बियाण्याखाली ५ सेमी खोलीवर द्यावे. कडधान्य पिके स्वतः नत्र स्थिर करतात."
                    else if (isHi) "100% DAP ($dapBags बोरी) + 100% MOP ($mopBags बोरी) + 100% यूरिया ($ureaBags बोरी) बीज के 5 सेमी नीचे दें। दलहनी फसलें स्वतः नाइट्रोजन स्थिर करती हैं।"
                    else "Apply 100% DAP ($dapBags bags) + 100% MOP ($mopBags bags) + 100% starter Urea ($ureaBags bags) placed 5cm below seed. Root nodules will fix N naturally."
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScheduleStageRow(
                    stageNumber = "2",
                    stageName = if (isMr) "फुलोरा अवस्था (३०-३५ दिवस)" else if (isHi) "फूल आने की अवस्था (30-35 दिन)" else "Flowering Stage (30-35 Days)",
                    instruction = if (isMr) "१९:१९:१९ विद्राव्य खत (१ किलो/एकर) किंवा २% DAP फवारणी करावी."
                    else if (isHi) "19:19:19 घुलनशील खाद (1 किग्रा/एकड़) या 2% DAP का पर्णीय छिड़काव करें।"
                    else "Foliar spray of 19:19:19 water-soluble fertilizer (1 kg/acre) or 2% DAP for flower retention."
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScheduleStageRow(
                    stageNumber = "3",
                    stageName = if (isMr) "शेंगा भरण्याची अवस्था (५०-५५ दिवस)" else if (isHi) "फलियां भरने की अवस्था (50-55 दिन)" else "Pod Formation (50-55 Days)",
                    instruction = if (isMr) "०:५२:३४ (१ किलो/एकर) आणि सूक्ष्मअन्नद्रव्य बोरोन फवारणी करावी."
                    else if (isHi) "0:52:34 (1 किग्रा/एकड़) एवं बोरॉन का छिड़काव दानों के भराव के लिए करें।"
                    else "Apply foliar 0:52:34 (1 kg/acre) + Boron (0.5%) for maximum grain and pod development."
                )
            } else {
                val basalUrea = Math.round(ureaBags * rdf.splitRatio.first * 10.0) / 10.0
                val top1Urea = Math.round(ureaBags * rdf.splitRatio.second * 10.0) / 10.0
                val top2Urea = Math.round(ureaBags * rdf.splitRatio.third * 10.0) / 10.0

                ScheduleStageRow(
                    stageNumber = "1",
                    stageName = if (isMr) "पेरणी वेळी (Basal Dressing)" else if (isHi) "बुवाई के समय (बेसल ड्रेसिंग)" else "Basal Dressing (At Sowing)",
                    instruction = if (isMr) "१००% DAP ($dapBags गोणी) + १००% MOP ($mopBags गोणी) + $basalUrea गोणी युरिया बियाण्याजवळ टाकावे."
                    else if (isHi) "100% DAP ($dapBags बोरी) + 100% MOP ($mopBags बोरी) + $basalUrea बोरी यूरिया बुवाई के समय दें।"
                    else "Apply 100% DAP ($dapBags bags) + 100% MOP ($mopBags bags) + $basalUrea bags Urea placed 5cm below seed."
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScheduleStageRow(
                    stageNumber = "2",
                    stageName = if (isMr) "पहिले खत (३० दिवस)" else if (isHi) "पहला टॉप ड्रेसिंग (30 दिन)" else "1st Top Dressing (30 Days)",
                    instruction = if (isMr) "$top1Urea गोणी युरिया हलक्या पाण्यासोबत द्यावे (फुटवे वाढीसाठी)."
                    else if (isHi) "$top1Urea बोरी यूरिया पहली हल्की सिंचाई के साथ दें (कल्ले बढ़ाने हेतु)।"
                    else "Broadcast $top1Urea bags Urea with first light irrigation after weeding."
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScheduleStageRow(
                    stageNumber = "3",
                    stageName = if (isMr) "दुसरे खत (५५ दिवस)" else if (isHi) "दूसरा टॉप ड्रेसिंग (55 दिन)" else "2nd Top Dressing (55 Days)",
                    instruction = if (isMr) "उर्वरित $top2Urea गोणी युरिया फुलोऱ्यापूर्वी टाकावे."
                    else if (isHi) "बचा हुआ $top2Urea बोरी यूरिया फूल आने से पूर्व छिड़कें।"
                    else "Broadcast remaining $top2Urea bags Urea prior to flowering stage."
                )
            }
        }
    }
}

@Composable
private fun SmartIrrigationTabContent(
    selectedCrop: String,
    acreage: Double,
    sowingDays: Int,
    onSowingDaysChange: (Int) -> Unit,
    soilMoisture: Float,
    onSoilMoistureChange: (Float) -> Unit,
    isMr: Boolean,
    isHi: Boolean
) {
    val totalDays = when (selectedCrop.lowercase()) {
        "sugarcane" -> 360
        "cotton" -> 160
        "soybean", "soyabean" -> 100
        "rice", "paddy" -> 130
        "onion" -> 140
        "potato" -> 110
        "tomato" -> 120
        "pumpkin" -> 115
        "maize" -> 110
        else -> 120
    }

    val progress = minOf(1.0f, sowingDays.toFloat() / totalDays.toFloat())

    // Determine crop stage
    val (currentStageName, waterCriticality) = when {
        sowingDays <= totalDays * 0.20 -> Pair("Early Vegetative / Crown Rooting", "Critical")
        sowingDays <= totalDays * 0.45 -> Pair("Active Tillering / Branching", "Moderate")
        sowingDays <= totalDays * 0.70 -> Pair("Flowering & Tasseling / Boll Formation", "High Criticality")
        sowingDays <= totalDays * 0.90 -> Pair("Grain / Fruit Filling", "Moderate")
        else -> Pair("Maturity & Pre-Harvest", "Low (Cut Off Water)")
    }

    // Determine water decision today
    val needsWaterToday = soilMoisture < 50f && !currentStageName.contains("Cut Off")
    val recommendedRunHours = if (needsWaterToday) {
        if (selectedCrop.equals("Sugarcane", true)) 2.5 else 1.5
    } else 0.0

    val litersPerAcre = if (needsWaterToday) {
        (recommendedRunHours * 8500).roundToInt()
    } else 0

    // STEP 1: Crop Growth & Age
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMr) "पिकाची वाढ आणि वय" else if (isHi) "फसल की वृद्धि एवं आयु" else "Step 1: Crop Growth & Age",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenSurface
                ) {
                    Text(
                        text = "$sowingDays / $totalDays Days",
                        color = GreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary,
                trackColor = GreenSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Current Stage: $currentStageName",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Water Need: $waterCriticality",
                    fontSize = 11.sp,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text("Days Since Sowing (Slide to adjust):", fontSize = 11.sp, color = TextSecondary)
            Slider(
                value = sowingDays.toFloat(),
                onValueChange = { onSowingDaysChange(it.toInt()) },
                valueRange = 1f..totalDays.toFloat(),
                colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary)
            )
        }
    }

    // STEP 2: Today's Watering Decision Card
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (needsWaterToday) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        ),
        border = BorderStroke(1.5.dp, if (needsWaterToday) GreenPrimary else OrangePrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (needsWaterToday) GreenPrimary else OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMr) "आज पाणी द्यावे का? (Water Today?)"
                        else if (isHi) "आज पानी देना है या नहीं? (Water Today?)"
                        else "Step 2: Water Decision Today",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (needsWaterToday) GreenPrimary else OrangePrimary
                ) {
                    Text(
                        text = if (needsWaterToday) "💧 YES - WATER TODAY" else "🚫 NO WATER NEEDED",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (needsWaterToday) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⏱️ Drip Duration", fontSize = 11.sp, color = TextSecondary)
                            Text("$recommendedRunHours Hours", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪣 Volume/Acre", fontSize = 11.sp, color = TextSecondary)
                            Text("~${litersPerAcre}L", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (isMr) "मातीतील ओलावा ${soilMoisture.roundToInt()}% (कमी) आहे आणि पुढील ४ दिवस पावसाची शक्यता नाही. आज सकाळी ६ ते ९ दरम्यान ठिबक सिंचन सुरू करा."
                    else if (isHi) "मिट्टी में नमी ${soilMoisture.roundToInt()}% (कम) है। आगामी 4 दिनों में वर्षा की संभावना नहीं है। आज प्रातः 6 से 9 बजे के मध्य ड्रिप चलाएं।"
                    else "Soil moisture is at ${soilMoisture.roundToInt()}% (Dry) with zero rain forecasted. Run drip irrigation today between 6:00 AM - 9:00 AM.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            } else {
                Text(
                    text = if (isMr) "मातीतील ओलावा ${soilMoisture.roundToInt()}% (पुरेसा) आहे. अतिरिक्त पाण्यामुळे मुळे कुजण्याचा धोका असतो. आज पाणी देणे टाळा."
                    else if (isHi) "मिट्टी में पर्याप्त नमी (${soilMoisture.roundToInt()}%) उपस्थित है। अधिक पानी से जड़ सड़न हो सकती है। आज सिंचाई न करें।"
                    else "Soil moisture is adequate (${soilMoisture.roundToInt()}%). Skipping watering prevents root rot and fertilizer leaching.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Soil Moisture Probe: ${soilMoisture.roundToInt()}%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Text(if (soilMoisture < 50f) "Dry Soil" else "Moist Soil", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (soilMoisture < 50f) Color(0xFFE53935) else GreenPrimary)
            }
            Slider(
                value = soilMoisture,
                onValueChange = onSoilMoistureChange,
                valueRange = 20f..85f,
                colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary)
            )
        }
    }

    // STEP 3: Simple 4-Step Practical Instructions
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("3", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMr) "पाणी देण्याची सोपी पद्धत (४ पायऱ्या)"
                    else if (isHi) "सिंचाई का सरल तरीका (4 कदम)"
                    else "Step 3: Easy 4-Step Watering Guide",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            ScheduleStageRow(
                stageNumber = "1",
                stageName = if (isMr) "मातीची तपासणी (Check Soil)" else if (isHi) "मिट्टी की जांच (Soil Check)" else "1. Check Soil Moisture",
                instruction = if (isMr) "मुळांजवळील २ इंच माती हातात घेऊन लाडू बनवा. माती कोरडी वाटल्यास पंप सुरू करा."
                else if (isHi) "जड़ के पास 2 इंच मिट्टी खोदकर देखें। यदि मिट्टी सूखी व भुरभुरी लगे तो ही मोटर चालू करें।"
                else "Dig 2 inches near crop roots. If soil is dry and crumbles easily, begin scheduled watering."
            )
            Spacer(modifier = Modifier.height(10.dp))

            ScheduleStageRow(
                stageNumber = "2",
                stageName = if (isMr) "योग्य वेळ (Ideal Timing)" else if (isHi) "सही समय (Ideal Timing)" else "2. Water at Right Hours",
                instruction = if (isMr) "सकाळी ६ ते ९ किंवा संध्याकाळी ५ नंतर पाणी द्या. दुपारी पाणी दिल्यास ४०% बाष्पीभवन होते."
                else if (isHi) "सुबह 6-9 बजे या शाम 5 बजे के बाद ही पानी दें। तेज धूप में 40% पानी भाप बनकर उड़ जाता है।"
                else "Operate pump between 6 AM - 9 AM or after 5 PM to prevent up to 40% evaporation loss."
            )
            Spacer(modifier = Modifier.height(10.dp))

            ScheduleStageRow(
                stageNumber = "3",
                stageName = if (isMr) "दाब व फिल्टर (Filter Check)" else if (isHi) "फिल्टर व दबाव (Pressure Check)" else "3. Check Drippers & Pressure",
                instruction = if (isMr) "स्क्रीन फिल्टर साफ ठेवा आणि दाबाचा काटा १.५ ते २.० बार असल्याची खात्री करा."
                else if (isHi) "स्क्रीन फिल्टर को साफ रखें और दबाव मीटर 1.5 - 2.0 बार पर बनाए रखें।"
                else "Ensure sand/screen filter is backwashed and operating pressure remains at 1.5 - 2.0 kg/cm²."
            )
            Spacer(modifier = Modifier.height(10.dp))

            ScheduleStageRow(
                stageNumber = "4",
                stageName = if (isMr) "खतांचे नियोजन (Fertigation)" else if (isHi) "घुलनशील खाद (Fertigation)" else "4. Water Soluble Fertigation",
                instruction = if (isMr) "पाणी सुरू केल्यानंतर २० मिनिटांनी व्हेंचुरीद्वारे विद्राव्य खत सोडावे."
                else if (isHi) "मोटर चलने के 20 मिनट बाद वेंचुरी द्वारा 19:19:19 या 0:52:34 खाद घोलकर दें।"
                else "Inject water-soluble fertilizers via venturi during the middle 50% of the irrigation run."
            )
        }
    }

    // STEP 4: 7-Day Weather-Adaptive Schedule
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("4", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMr) "७ दिवसांचे हवामान व पाणी वेळापत्रक"
                    else if (isHi) "7 दिवसीय मौसम आधारित सिंचाई कैलेंडर"
                    else "Step 4: 7-Day Weather & Irrigation Schedule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            val weekDays = listOf(
                Triple("Today (Day 1)", "☀️ 32°C Clear", "💧 Drip 1.5 Hours"),
                Triple("Tomorrow (Day 2)", "☀️ 33°C Sunny", "🚫 Field Rest (No Water)"),
                Triple("Day 3", "⛅ 30°C Partly Cloudy", "🚫 Field Rest (No Water)"),
                Triple("Day 4", "☀️ 31°C Clear", "💧 Drip 1.5 Hours"),
                Triple("Day 5", "⛅ 29°C Humid", "🚫 Field Rest (No Water)"),
                Triple("Day 6", "🌧️ 25°C Rain Forecast", "🚫 Rain Day (Skip Water)"),
                Triple("Day 7", "⛅ 28°C Post-Rain", "💧 Light 45 Min (If Dry)")
            )

            weekDays.forEach { (day, weather, action) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(day, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text(weather, fontSize = 11.sp, color = TextSecondary)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (action.contains("Drip")) GreenSurface else Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = action,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (action.contains("Drip")) GreenDark else TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Divider(color = Color(0xFFF5F5F5))
            }
        }
    }
}

@Composable
private fun NutrientStatusBadge(status: String) {
    val (color, bgColor) = when {
        status.contains("Low") -> Color(0xFFE53935) to Color(0xFFFFEBEE)
        status.contains("High") -> Color(0xFFFF9800) to Color(0xFFFFF3E0)
        else -> GreenPrimary to GreenSurface
    }
    Surface(shape = RoundedCornerShape(6.dp), color = bgColor) {
        Text(
            text = status,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun FertilizerBagItem(
    modifier: Modifier = Modifier,
    title: String,
    bags: Double,
    color: Color,
    emoji: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${bags} Bags",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScheduleStageRow(stageNumber: String, stageName: String, instruction: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(stageNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stageName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(instruction, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
        }
    }
}
