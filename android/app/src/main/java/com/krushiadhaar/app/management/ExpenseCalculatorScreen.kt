package com.krushiadhaar.app.management

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.GlobalMockData.PersonalExpense
import com.krushiadhaar.app.ui.theme.*

// --- Data Models ---

data class ResourceDefinition(
    val id: String,
    val title: String,
    val regionalName: String,
    val defaultShare: Float,
    val color: Color,
    val icon: ImageVector
)

data class ResourceBreakdown(
    val id: String,
    val label: String,
    val regionalName: String,
    val percent: Float,
    val amount: Int,
    val color: Color,
    val icon: ImageVector,
    val calculationFormula: String = ""
)

data class CropAgronomicModel(
    val id: String,
    val cropName: String,
    val regionalName: String,
    val emoji: String,
    val defaultYieldQuintalsPerAcre: Double,
    val mspPricePerQuintal: Int,
    val seedRatePerAcre: Double,
    val seedUnit: String,
    val seedPricePerUnit: Int,
    val seedVarietyExample: String,
    val fertilizerCostPerAcre: Int,
    val fertilizerDoseDesc: String,
    val pesticideCostPerAcre: Int,
    val pesticideSpraysDesc: String,
    val laborMandaysPerAcre: Int,
    val laborDailyWage: Int,
    val laborDesc: String,
    val tractorHoursPerAcre: Double,
    val tractorHourlyRate: Int,
    val tractorOpsDesc: String,
    val irrigationRoundsPerAcre: Int,
    val irrigationCostPerRound: Int,
    val irrigationDesc: String,
    val harvestingCostPerAcre: Int,
    val harvestingDesc: String,
    val transportFreightPerQuintal: Int,
    val bagCostPerQuintal: Int
) {
    fun calculateResourceCost(resourceId: String, acres: Double): Pair<Int, String> {
        val a = acres.coerceAtLeast(0.0)
        return when (resourceId) {
            "seeds" -> {
                val totalQty = seedRatePerAcre * a
                val cost = (totalQty * seedPricePerUnit).toInt()
                val unitStr = if (seedUnit == "kg") "kg" else seedUnit
                val qtyStr = if (totalQty % 1.0 == 0.0) "${totalQty.toInt()}" else String.format("%.1f", totalQty)
                cost to "$qtyStr $unitStr ($seedVarietyExample @ \u20B9$seedPricePerUnit/$unitStr)"
            }
            "fertilizer" -> {
                val cost = (fertilizerCostPerAcre * a).toInt()
                val bags = (3 * a).toInt().coerceAtLeast(1)
                cost to "$bags Bags ($fertilizerDoseDesc @ \u20B9$fertilizerCostPerAcre/Ac)"
            }
            "pesticides" -> {
                val cost = (pesticideCostPerAcre * a).toInt()
                cost to "$pesticideSpraysDesc (@ \u20B9$pesticideCostPerAcre/Ac)"
            }
            "labor" -> {
                val days = (laborMandaysPerAcre * a).toInt()
                val cost = days * laborDailyWage
                cost to "$days Mandays ($laborDesc @ \u20B9$laborDailyWage/day)"
            }
            "machinery" -> {
                val hours = tractorHoursPerAcre * a
                val cost = (hours * tractorHourlyRate).toInt()
                val hrsStr = if (hours % 1.0 == 0.0) "${hours.toInt()}" else String.format("%.1f", hours)
                cost to "$hrsStr Tractor Hrs ($tractorOpsDesc @ \u20B9$tractorHourlyRate/hr)"
            }
            "irrigation" -> {
                val rounds = (irrigationRoundsPerAcre * a).toInt()
                val cost = rounds * irrigationCostPerRound
                cost to "$rounds Watering Rounds ($irrigationDesc @ \u20B9$irrigationCostPerRound/round)"
            }
            "harvesting" -> {
                val cost = (harvestingCostPerAcre * a).toInt()
                cost to "$harvestingDesc (@ \u20B9$harvestingCostPerAcre/Ac)"
            }
            "transport" -> {
                val yield = defaultYieldQuintalsPerAcre * a
                val cost = (yield * transportFreightPerQuintal).toInt()
                cost to "${String.format("%.1f", yield)} Qtl Freight to Mandi (@ \u20B9$transportFreightPerQuintal/Qtl)"
            }
            "storage" -> {
                val yield = defaultYieldQuintalsPerAcre * a
                val bagsNeeded = yield.toInt().coerceAtLeast(1)
                val cost = bagsNeeded * bagCostPerQuintal
                cost to "$bagsNeeded Gunny Bags (50kg cap @ \u20B9$bagCostPerQuintal/bag)"
            }
            else -> 0 to ""
        }
    }

    fun getTotalCostPerAcre(): Int {
        val seed = (seedRatePerAcre * seedPricePerUnit).toInt()
        val fert = fertilizerCostPerAcre
        val pest = pesticideCostPerAcre
        val lab = laborMandaysPerAcre * laborDailyWage
        val mach = (tractorHoursPerAcre * tractorHourlyRate).toInt()
        val irr = irrigationRoundsPerAcre * irrigationCostPerRound
        val harv = harvestingCostPerAcre
        val trans = (defaultYieldQuintalsPerAcre * transportFreightPerQuintal).toInt()
        val stor = (defaultYieldQuintalsPerAcre * bagCostPerQuintal).toInt()
        return seed + fert + pest + lab + mach + irr + harv + trans + stor
    }
}

// Clean Indian Rupee Number Formatting: 40000 -> "₹40,000", 140000 -> "₹1,40,000", 1234567 -> "₹12,34,567"
fun formatRupees(amount: Number, includeSymbol: Boolean = true): String {
    val num = amount.toLong()
    val isNegative = num < 0
    val absVal = kotlin.math.abs(num)
    val s = absVal.toString()
    val formatted = if (s.length <= 3) {
        s
    } else {
        val lastThree = s.takeLast(3)
        val rest = s.dropLast(3)
        val buffer = StringBuilder()
        for (i in rest.indices) {
            buffer.append(rest[i])
            val remainingDigits = rest.length - 1 - i
            if (remainingDigits > 0 && remainingDigits % 2 == 0) {
                buffer.append(",")
            }
        }
        buffer.append(",").append(lastThree)
        buffer.toString()
    }
    val sign = if (isNegative) "-" else ""
    return if (includeSymbol) "$sign\u20B9$formatted" else "$sign$formatted"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCalculatorScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("🌾 Crop Cost", "📒 Personal Ledger", "📊 Financial Tally")

    // --- Scientific Agronomic Crop Database ---
    val agronomicCropList = remember {
        listOf(
            CropAgronomicModel(
                id = "soybean",
                cropName = "Soybean (सोयाबीन)",
                regionalName = "सोयाबीन",
                emoji = "🌱",
                defaultYieldQuintalsPerAcre = 11.0,
                mspPricePerQuintal = 4892,
                seedRatePerAcre = 30.0,
                seedUnit = "kg",
                seedPricePerUnit = 95,
                seedVarietyExample = "JS-335 / KDS-726",
                fertilizerCostPerAcre = 3700,
                fertilizerDoseDesc = "1 Bag DAP + 1 Potash + 1 SSP",
                pesticideCostPerAcre = 2200,
                pesticideSpraysDesc = "Pre-emergence herbicide + 2 foliar sprays",
                laborMandaysPerAcre = 20,
                laborDailyWage = 350,
                laborDesc = "Sowing, 2 weedings & harvest",
                tractorHoursPerAcre = 3.5,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Ploughing, rotavator & drill",
                irrigationRoundsPerAcre = 3,
                irrigationCostPerRound = 450,
                irrigationDesc = "Flowering & pod filling waterings",
                harvestingCostPerAcre = 2600,
                harvestingDesc = "Combine harvester cutting & threshing",
                transportFreightPerQuintal = 75,
                bagCostPerQuintal = 45
            ),
            CropAgronomicModel(
                id = "wheat",
                cropName = "Wheat (गहू)",
                regionalName = "गहू",
                emoji = "🌾",
                defaultYieldQuintalsPerAcre = 18.0,
                mspPricePerQuintal = 2275,
                seedRatePerAcre = 40.0,
                seedUnit = "kg",
                seedPricePerUnit = 45,
                seedVarietyExample = "Lokwan / HD-2967 certified",
                fertilizerCostPerAcre = 3600,
                fertilizerDoseDesc = "2 Bags Urea + 1 Bag DAP + Potash",
                pesticideCostPerAcre = 1200,
                pesticideSpraysDesc = "Weedicide 2,4-D + rust protection",
                laborMandaysPerAcre = 12,
                laborDailyWage = 350,
                laborDesc = "Sowing & basin channels",
                tractorHoursPerAcre = 3.0,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Ploughing, harrowing & leveling",
                irrigationRoundsPerAcre = 5,
                irrigationCostPerRound = 450,
                irrigationDesc = "Crown root, tillering & milking stages",
                harvestingCostPerAcre = 2400,
                harvestingDesc = "Combine harvester threshing",
                transportFreightPerQuintal = 60,
                bagCostPerQuintal = 45
            ),
            CropAgronomicModel(
                id = "cotton",
                cropName = "Cotton (कापूस)",
                regionalName = "कापूस",
                emoji = "🪴",
                defaultYieldQuintalsPerAcre = 10.0,
                mspPricePerQuintal = 7121,
                seedRatePerAcre = 2.0,
                seedUnit = "packets",
                seedPricePerUnit = 864,
                seedVarietyExample = "Bollgard II Hybrid BG-II",
                fertilizerCostPerAcre = 5100,
                fertilizerDoseDesc = "3 Bags Urea + 2 Bags NPK 10:26:26",
                pesticideCostPerAcre = 4500,
                pesticideSpraysDesc = "Sucking pest + Pink bollworm 4 sprays",
                laborMandaysPerAcre = 30,
                laborDailyWage = 350,
                laborDesc = "Sowing + 5 manual cotton pickings",
                tractorHoursPerAcre = 3.5,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Tillage & inter-cultivation weeding",
                irrigationRoundsPerAcre = 4,
                irrigationCostPerRound = 450,
                irrigationDesc = "Drip fertigation & boll watering",
                harvestingCostPerAcre = 0,
                harvestingDesc = "Included in picking labor",
                transportFreightPerQuintal = 100,
                bagCostPerQuintal = 60
            ),
            CropAgronomicModel(
                id = "sugarcane",
                cropName = "Sugarcane (ऊस)",
                regionalName = "ऊस",
                emoji = "🎋",
                defaultYieldQuintalsPerAcre = 500.0,
                mspPricePerQuintal = 315,
                seedRatePerAcre = 25000.0,
                seedUnit = "setts",
                seedPricePerUnit = 1,
                seedVarietyExample = "Co 86032 two-eye setts",
                fertilizerCostPerAcre = 14500,
                fertilizerDoseDesc = "Urea, DAP, Potash & 5 Tons FYM compost",
                pesticideCostPerAcre = 3200,
                pesticideSpraysDesc = "Trash mulching & white grub control",
                laborMandaysPerAcre = 35,
                laborDailyWage = 350,
                laborDesc = "Planting, earthing-up & weeding",
                tractorHoursPerAcre = 5.5,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Subsoiler, ridger & furrow operations",
                irrigationRoundsPerAcre = 25,
                irrigationCostPerRound = 300,
                irrigationDesc = "Year-round drip fertigation & canal",
                harvestingCostPerAcre = 22500,
                harvestingDesc = "Sugar factory cutting & loader contract",
                transportFreightPerQuintal = 0,
                bagCostPerQuintal = 0
            ),
            CropAgronomicModel(
                id = "rice",
                cropName = "Rice / Paddy (भात)",
                regionalName = "भात",
                emoji = "🌾",
                defaultYieldQuintalsPerAcre = 22.0,
                mspPricePerQuintal = 2300,
                seedRatePerAcre = 25.0,
                seedUnit = "kg",
                seedPricePerUnit = 65,
                seedVarietyExample = "MTU 1010 / Indrayani certified",
                fertilizerCostPerAcre = 4200,
                fertilizerDoseDesc = "Urea, DAP, Zinc Sulphate & Potash",
                pesticideCostPerAcre = 2400,
                pesticideSpraysDesc = "Stem borer, leaf folder & blast control",
                laborMandaysPerAcre = 25,
                laborDailyWage = 350,
                laborDesc = "Nursery, transplantation & hand weeding",
                tractorHoursPerAcre = 4.0,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Puddling rotor, cage wheels & tilling",
                irrigationRoundsPerAcre = 8,
                irrigationCostPerRound = 450,
                irrigationDesc = "Standing water maintenance & canal",
                harvestingCostPerAcre = 2800,
                harvestingDesc = "Paddy combine harvester",
                transportFreightPerQuintal = 65,
                bagCostPerQuintal = 45
            ),
            CropAgronomicModel(
                id = "onion",
                cropName = "Onion (कांदा)",
                regionalName = "कांदा",
                emoji = "🧅",
                defaultYieldQuintalsPerAcre = 80.0,
                mspPricePerQuintal = 1800,
                seedRatePerAcre = 4.0,
                seedUnit = "kg",
                seedPricePerUnit = 1400,
                seedVarietyExample = "Phule Samarth / F1 Hybrid seed",
                fertilizerCostPerAcre = 6500,
                fertilizerDoseDesc = "NPK 10:26:26 + Sulphur + Urea + FYM",
                pesticideCostPerAcre = 3800,
                pesticideSpraysDesc = "Thrips & purple blotch fungicide sprays",
                laborMandaysPerAcre = 35,
                laborDailyWage = 350,
                laborDesc = "Transplanting seedlings & digging/curing",
                tractorHoursPerAcre = 3.5,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Deep tillage & raised bed former",
                irrigationRoundsPerAcre = 10,
                irrigationCostPerRound = 400,
                irrigationDesc = "Micro-sprinkler & drip cycles",
                harvestingCostPerAcre = 4000,
                harvestingDesc = "Manual harvesting, de-topping & sorting",
                transportFreightPerQuintal = 50,
                bagCostPerQuintal = 35
            ),
            CropAgronomicModel(
                id = "maize",
                cropName = "Maize (मका)",
                regionalName = "मका",
                emoji = "🌽",
                defaultYieldQuintalsPerAcre = 25.0,
                mspPricePerQuintal = 2225,
                seedRatePerAcre = 8.0,
                seedUnit = "kg",
                seedPricePerUnit = 280,
                seedVarietyExample = "Pioneer / Syngenta Hybrid",
                fertilizerCostPerAcre = 3800,
                fertilizerDoseDesc = "DAP + Urea + Zinc Sulphate",
                pesticideCostPerAcre = 1800,
                pesticideSpraysDesc = "Fall Armyworm (FAW) Emamectin sprays",
                laborMandaysPerAcre = 14,
                laborDailyWage = 350,
                laborDesc = "Sowing, weeding & cob detasseling",
                tractorHoursPerAcre = 3.0,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Plowing & ridging operations",
                irrigationRoundsPerAcre = 4,
                irrigationCostPerRound = 450,
                irrigationDesc = "Knee-high, tasseling & silking stages",
                harvestingCostPerAcre = 2200,
                harvestingDesc = "Cob picker & mechanical shelling thresher",
                transportFreightPerQuintal = 60,
                bagCostPerQuintal = 40
            ),
            CropAgronomicModel(
                id = "gram",
                cropName = "Gram / Chana (हरभरा)",
                regionalName = "हरभरा",
                emoji = "🫘",
                defaultYieldQuintalsPerAcre = 9.0,
                mspPricePerQuintal = 5440,
                seedRatePerAcre = 30.0,
                seedUnit = "kg",
                seedPricePerUnit = 85,
                seedVarietyExample = "Digvijay / JG-11 certified",
                fertilizerCostPerAcre = 2400,
                fertilizerDoseDesc = "DAP 18:46 + Rhizobium culture bio-dose",
                pesticideCostPerAcre = 1600,
                pesticideSpraysDesc = "Helicoverpa pod borer pheromone + spray",
                laborMandaysPerAcre = 12,
                laborDailyWage = 350,
                laborDesc = "Sowing & manual weeding labor",
                tractorHoursPerAcre = 2.5,
                tractorHourlyRate = 1200,
                tractorOpsDesc = "Conservation tillage & seed drill",
                irrigationRoundsPerAcre = 2,
                irrigationCostPerRound = 450,
                irrigationDesc = "Branching & pod development waterings",
                harvestingCostPerAcre = 2000,
                harvestingDesc = "Manual sickle cutting & stationary thresher",
                transportFreightPerQuintal = 70,
                bagCostPerQuintal = 45
            )
        )
    }

    // Auto-fill from Farmer Profile
    val initialProfileCrop = remember {
        agronomicCropList.find {
            it.cropName.contains(GlobalMockData.farmerCrop.value, ignoreCase = true) ||
            it.regionalName.contains(GlobalMockData.farmerCrop.value, ignoreCase = true) ||
            GlobalMockData.farmerCrop.value.contains(it.id, ignoreCase = true)
        } ?: agronomicCropList[0]
    }

    var selectedCropModel by remember { mutableStateOf(initialProfileCrop) }
    var cropExpanded by remember { mutableStateOf(false) }
    var farmAreaText by remember { mutableStateOf(GlobalMockData.farmerFarmArea.value) }

    // Expanded 9 Farming Resources
    val allResources = remember {
        listOf(
            ResourceDefinition("seeds", "Seeds & Seedlings", "बियाणे", 0.12f, OrangePrimary, Icons.Default.Eco),
            ResourceDefinition("fertilizer", "Fertilizers & Nutrients", "रासायनिक व सेंद्रिय खते", 0.22f, GreenPrimary, Icons.Default.Grass),
            ResourceDefinition("pesticides", "Pesticides & Protection", "कीटकनाशके व तणनाशके", 0.10f, Color(0xFF8E24AA), Icons.Default.BugReport),
            ResourceDefinition("labor", "Labor & Farm Hands", "मजुरी व फवारणी खर्च", 0.24f, Color(0xFF1E88E5), Icons.Default.Group),
            ResourceDefinition("machinery", "Machinery & Tractor Rent", "ट्रॅक्टर व अवजारे भाडे", 0.12f, Color(0xFFD84315), Icons.Default.Agriculture),
            ResourceDefinition("irrigation", "Irrigation & Water Pump", "सिंचन, वीज व पाणी", 0.08f, Color(0xFF00ACC1), Icons.Default.WaterDrop),
            ResourceDefinition("harvesting", "Harvesting & Threshing", "कापणी व मळणी खर्च", 0.06f, Color(0xFFF57C00), Icons.Default.ContentCut),
            ResourceDefinition("transport", "Transport & Mandi Freight", "वाहतूक व बाजार हमाली", 0.04f, Color(0xFF3949AB), Icons.Default.LocalShipping),
            ResourceDefinition("storage", "Storage & Packaging Bags", "बारदाणा व साठवणूक", 0.02f, Color(0xFF5D4037), Icons.Default.Inventory2)
        )
    }

    val selectedResourceMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            allResources.forEach { put(it.id, true) }
        }
    }

    var showResult by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addDialogIsExpense by remember { mutableStateOf(true) }
    var addDialogInitialCategory by remember { mutableStateOf<String?>(null) }

    // Area and agronomic scientific calculations
    val area = farmAreaText.toDoubleOrNull() ?: 0.0
    val basePerAcre = selectedCropModel.getTotalCostPerAcre()

    val rawBreakdowns = allResources
        .filter { selectedResourceMap[it.id] == true }
        .map { res ->
            val (cost, formula) = selectedCropModel.calculateResourceCost(res.id, area)
            Triple(res, cost, formula)
        }

    val totalCalculatedCropBudget = rawBreakdowns.sumOf { it.second }

    val breakdowns = rawBreakdowns.map { (res, amt, formula) ->
        val percent = if (totalCalculatedCropBudget > 0) amt.toFloat() / totalCalculatedCropBudget else 0f
        ResourceBreakdown(
            id = res.id,
            label = res.title,
            regionalName = res.regionalName,
            percent = percent,
            amount = amt,
            color = res.color,
            icon = res.icon,
            calculationFormula = formula
        )
    }

    // Revenue, Yield & Net Profit Estimation
    val expectedYieldQtl = selectedCropModel.defaultYieldQuintalsPerAcre * area
    val expectedRevenue = (expectedYieldQtl * selectedCropModel.mspPricePerQuintal).toInt()
    val netFarmProfit = expectedRevenue - totalCalculatedCropBudget
    val roiPercent = if (totalCalculatedCropBudget > 0) (((netFarmProfit.toDouble() / totalCalculatedCropBudget)) * 100).toInt() else 0

    // Update global reference when calculated
    LaunchedEffect(totalCalculatedCropBudget, selectedCropModel.cropName, farmAreaText) {
        if (totalCalculatedCropBudget > 0) {
            val cropSimpleName = selectedCropModel.cropName.substringBefore(" (")
            GlobalMockData.updateCropEstimate(totalCalculatedCropBudget, cropSimpleName, farmAreaText)
        }
    }

    // --- Finances & Tally Aggregates ---
    val personalList = GlobalMockData.personalExpenses
    val personalOutflows = personalList.filter { it.isExpense }.sumOf { it.amount }
    val personalInflows = personalList.filter { !it.isExpense }.sumOf { it.amount }
    val cropYieldRevenue = GlobalMockData.cropYieldRevenue.value

    val grandTotalOutflows = totalCalculatedCropBudget + personalOutflows
    val grandTotalInflows = cropYieldRevenue + personalInflows
    val netTallyBalance = grandTotalInflows - grandTotalOutflows

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Expenses & Farm Finances",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "खर्च हिशोब, वैयक्तिक खाते आणि ताळेबंद",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Financial Tally is up-to-date", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet", tint = GreenText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Tally Summary Card (Always Visible Header)
            TallyHeaderCard(
                totalOutflows = grandTotalOutflows,
                totalInflows = grandTotalInflows,
                netBalance = netTallyBalance,
                cropExpense = totalCalculatedCropBudget,
                personalExpense = personalOutflows
            )

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenDark,
                divider = { Divider(color = AppDivider) }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        },
                        selectedContentColor = GreenDark,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> CropCostCalculatorTab(
                        agronomicCrops = agronomicCropList,
                        selectedCropModel = selectedCropModel,
                        cropExpanded = cropExpanded,
                        onCropExpandedChange = { cropExpanded = it },
                        onSelectCrop = { selectedCropModel = it },
                        farmAreaText = farmAreaText,
                        onFarmAreaChange = { farmAreaText = it },
                        onResetToProfile = {
                            selectedCropModel = initialProfileCrop
                            farmAreaText = GlobalMockData.farmerFarmArea.value
                            Toast.makeText(context, "Synced with ${GlobalMockData.farmerName.value}'s profile!", Toast.LENGTH_SHORT).show()
                        },
                        allResources = allResources,
                        selectedResourceMap = selectedResourceMap,
                        totalBudget = totalCalculatedCropBudget,
                        breakdowns = breakdowns,
                        perAcreCost = basePerAcre,
                        expectedYieldQtl = expectedYieldQtl,
                        expectedRevenue = expectedRevenue,
                        netFarmProfit = netFarmProfit,
                        roiPercent = roiPercent,
                        showResult = showResult,
                        onCalculate = {
                            showResult = true
                            val cropSimpleName = selectedCropModel.cropName.substringBefore(" (")
                            GlobalMockData.updateCropEstimate(totalCalculatedCropBudget, cropSimpleName, farmAreaText)
                            Toast.makeText(context, "Calculated & synced with Financial Tally!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    1 -> PersonalLedgerTab(
                        expenses = personalList,
                        onAddClick = {
                            addDialogIsExpense = true
                            addDialogInitialCategory = "Household"
                            showAddDialog = true
                        },
                        onDeleteClick = { id -> GlobalMockData.removePersonalExpense(id) }
                    )
                    2 -> FinancialTallyTab(
                        cropBudget = totalCalculatedCropBudget,
                        cropName = selectedCropModel.cropName.substringBefore(" ("),
                        personalOutflow = personalOutflows,
                        personalInflow = personalInflows,
                        marketplaceRevenue = cropYieldRevenue,
                        netBalance = netTallyBalance,
                        expensesList = personalList,
                        onGoToCalculator = { selectedTab = 0 },
                        onGoToPersonal = { selectedTab = 1 },
                        onAddRecord = { isExp, cat ->
                            addDialogIsExpense = isExp
                            addDialogInitialCategory = cat
                            showAddDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            initialIsExpense = addDialogIsExpense,
            initialCategory = addDialogInitialCategory,
            onDismiss = { showAddDialog = false },
            onSave = { expense ->
                GlobalMockData.addPersonalExpense(expense)
                showAddDialog = false
                Toast.makeText(context, "Record added to finances!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// 1. TALLY HEADER OVERVIEW CARD
// ==========================================

@Composable
private fun TallyHeaderCard(
    totalOutflows: Int,
    totalInflows: Int,
    netBalance: Int,
    cropExpense: Int,
    personalExpense: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = GreenText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Farmer's Financial Tally (ताळेबंद)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Status chip
                val isProfit = netBalance >= 0
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isProfit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (isProfit) "Surplus / नफा" else "Deficit / तूट",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isProfit) GreenDark else RedError,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Total Inflow (जमा)", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = formatRupees(totalInflows),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenText
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Total Outflow (खर्च)", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = formatRupees(totalOutflows),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedError
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Net Balance (शिल्लक)", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = formatRupees(netBalance),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (netBalance >= 0) GreenDark else RedError
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-breakdown mini pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(AppBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🌾 Crop Inputs: ${formatRupees(cropExpense)}",
                    fontSize = 11.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "🏠 Personal & Farm: ${formatRupees(personalExpense)}",
                    fontSize = 11.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// 2. TAB 0: CROP COST CALCULATOR
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropCostCalculatorTab(
    agronomicCrops: List<CropAgronomicModel>,
    selectedCropModel: CropAgronomicModel,
    cropExpanded: Boolean,
    onCropExpandedChange: (Boolean) -> Unit,
    onSelectCrop: (CropAgronomicModel) -> Unit,
    farmAreaText: String,
    onFarmAreaChange: (String) -> Unit,
    onResetToProfile: () -> Unit,
    allResources: List<ResourceDefinition>,
    selectedResourceMap: MutableMap<String, Boolean>,
    totalBudget: Int,
    breakdowns: List<ResourceBreakdown>,
    perAcreCost: Int,
    expectedYieldQtl: Double,
    expectedRevenue: Int,
    netFarmProfit: Int,
    roiPercent: Int,
    showResult: Boolean,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Farmer Profile Auto-Fill Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GreenSurface),
            border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedCropModel.emoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Auto-filled: ${GlobalMockData.farmerName.value}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GreenDark
                        )
                        Text(
                            text = "Registered Farm: ${GlobalMockData.farmerCrop.value} • ${GlobalMockData.farmerFarmArea.value} Acres (${GlobalMockData.farmerDistrict.value})",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                OutlinedButton(
                    onClick = onResetToProfile,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp),
                    border = BorderStroke(1.dp, GreenPrimary)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, tint = GreenDark, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync", fontSize = 11.sp, color = GreenDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Crop Selector Dropdown
        Column {
            Text(
                text = "Select Crop Type (पीक निवडा)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            ExposedDropdownMenuBox(
                expanded = cropExpanded,
                onExpandedChange = onCropExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedCropModel.cropName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = AppDivider,
                        focusedBorderColor = GreenPrimary
                    )
                )
                ExposedDropdownMenu(
                    expanded = cropExpanded,
                    onDismissRequest = { onCropExpandedChange(false) }
                ) {
                    agronomicCrops.forEach { crop ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(crop.emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(crop.cropName, fontWeight = FontWeight.Medium)
                                    }
                                    Text(
                                        "${formatRupees(crop.getTotalCostPerAcre())}/Ac",
                                        color = GreenText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            onClick = {
                                onSelectCrop(crop)
                                onCropExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }

        // Farm Area Input + Quick Presets
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Farm Area in Acres (शेत जमीन एकर)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Scientific Rate: ${formatRupees(perAcreCost)}/Acre",
                    fontSize = 12.sp,
                    color = GreenText,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = farmAreaText,
                onValueChange = onFarmAreaChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Enter farm area in acres") },
                trailingIcon = { Text("Acres  ", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = AppDivider,
                    focusedBorderColor = GreenPrimary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("1", "2", "3", "5", "10").forEach { preset ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onFarmAreaChange(preset) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (farmAreaText == preset) GreenSurface else Color.White,
                        border = BorderStroke(
                            1.dp,
                            if (farmAreaText == preset) GreenPrimary else AppDivider
                        )
                    ) {
                        Text(
                            text = "$preset Ac",
                            fontSize = 12.sp,
                            fontWeight = if (farmAreaText == preset) FontWeight.Bold else FontWeight.Normal,
                            color = if (farmAreaText == preset) GreenDark else TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Expanded Required Resources
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Required Farming Resources (लागणारी संसाधने)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Real Agronomic Model • Toggle inputs to recalculate",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Quick Select All / Reset
                TextButton(
                    onClick = {
                        val allChecked = allResources.all { selectedResourceMap[it.id] == true }
                        allResources.forEach { selectedResourceMap[it.id] = !allChecked }
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    val allChecked = allResources.all { selectedResourceMap[it.id] == true }
                    Text(
                        text = if (allChecked) "Deselect All" else "Select All",
                        fontSize = 11.sp,
                        color = GreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2-Column Grid of 9 Resources
            val chunked = allResources.chunked(2)
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { resource ->
                        val isChecked = selectedResourceMap[resource.id] ?: true
                        ResourceCheckboxCard(
                            resource = resource,
                            checked = isChecked,
                            onCheckedChange = { selectedResourceMap[resource.id] = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Calculate Button
        Button(
            onClick = onCalculate,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Icon(Icons.Default.Calculate, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Calculate & Sync Crop Cost",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Result Card with Scientific Breakdown
        if (showResult && totalBudget > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Crop Budget (अंदाजित खर्च)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenDark
                            )
                            Text(
                                text = "${selectedCropModel.cropName.substringBefore(" (")} • $farmAreaText Acres (Agronomic Calculated)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenSurface
                        ) {
                            Text(
                                text = "${formatRupees(perAcreCost)} / Ac",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = formatRupees(totalBudget),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-Segment Stacked Resource Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(AppDivider)
                    ) {
                        breakdowns.forEach { item ->
                            if (item.percent > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(item.percent)
                                        .fillMaxHeight()
                                        .background(item.color)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Resource-wise Cost Distribution & Math (सविस्तर हिशोब):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    breakdowns.forEach { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = item.regionalName,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "${(item.percent * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Text(
                                    text = formatRupees(item.amount),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            // Calculation formula / unit details
                            if (item.calculationFormula.isNotBlank()) {
                                Text(
                                    text = "↳ ${item.calculationFormula}",
                                    fontSize = 10.sp,
                                    color = GreenDark,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 18.dp, top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Expected Yield, Gross MSP Revenue & Net Profit Estimation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📈", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Estimated Yield & Net Farm Profit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "हंगाम उत्पादन व निव्वळ नफा अंदाज (आधारभूत किंमत / MSP)",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = AppDivider)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Expected Yield", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${String.format("%.1f", expectedYieldQtl)} Qtl",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(text = "@ ${selectedCropModel.defaultYieldQuintalsPerAcre} Qtl/Ac", fontSize = 10.sp, color = TextMuted)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gross MSP Income", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = formatRupees(expectedRevenue),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                            Text(text = "@ \u20B9${selectedCropModel.mspPricePerQuintal}/Qtl", fontSize = 10.sp, color = TextMuted)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Cultivation Cost", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = formatRupees(totalBudget),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedError
                            )
                            Text(text = "${breakdowns.size} Inputs", fontSize = 10.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Net Profit Highlight Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (netFarmProfit >= 0) GreenSurface else Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, if (netFarmProfit >= 0) GreenPrimary else RedError.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (netFarmProfit >= 0) "Estimated Net Profit (निव्वळ नफा)" else "Estimated Deficit (अंदाजित तोटा)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (netFarmProfit >= 0) GreenDark else RedError
                                )
                                Text(
                                    text = formatRupees(netFarmProfit),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (netFarmProfit >= 0) GreenDark else RedError
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (netFarmProfit >= 0) GreenPrimary else RedError
                            ) {
                                Text(
                                    text = "ROI: ${if (roiPercent >= 0) "+" else ""}$roiPercent%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Smart Farm Inventory Connection Banner
            val matchingInventory = GlobalMockData.inventoryItems.filter { item ->
                item.category == "Fertilizers" || item.category == "Seeds"
            }
            if (matchingInventory.isNotEmpty()) {
                val invWorth = matchingInventory.sumOf { (it.quantity * it.pricePerUnit).toInt() }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                    border = BorderStroke(1.dp, Color(0xFFFFF59D))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Farm Inventory Connected (साठा उपलब्ध):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFF57F17)
                            )
                            Text(
                                text = "You have ${matchingInventory.size} items in your Farm Inventory (worth ${formatRupees(invWorth)}). Using your existing stock saves upfront cash expenses!",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceCheckboxCard(
    resource: ResourceDefinition,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) },
        color = if (checked) Color(0xFFF9FBF7) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (checked) GreenPrimary else AppDivider
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = GreenPrimary,
                    uncheckedColor = TextMuted
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resource.regionalName,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================
// 3. TAB 1: PERSONAL & OTHER EXPENSES (LEDGER)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalLedgerTab(
    expenses: List<PersonalExpense>,
    onAddClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Household", "Farm Operations", "Loan & EMI", "Livestock & Dairy", "Utilities", "Health & Education", "Farm Income")

    val filteredList = remember(expenses, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") expenses
        else if (selectedCategoryFilter == "Farm Income") expenses.filter { !it.isExpense }
        else expenses.filter { it.category == selectedCategoryFilter }
    }

    val totalExpenses = expenses.filter { it.isExpense }.sumOf { it.amount }
    val totalIncome = expenses.filter { !it.isExpense }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Card + Action Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Farmer's Personal Ledger",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "घरगुती व इतर दैनंदिन जमा-खर्च नोंदवही",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onAddClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add Entry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Personal Expenses", fontSize = 10.sp, color = RedError)
                            Text(
                                text = formatRupees(totalExpenses),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedError
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = GreenSurface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "Other Incomes", fontSize = 10.sp, color = GreenText)
                            Text(
                                text = formatRupees(totalIncome),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategoryFilter == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryFilter = category },
                    label = { Text(category, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expenses List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No records in this category",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap '+ Add Entry' to record personal expenses or income",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.id }) { item ->
                    PersonalExpenseRow(
                        item = item,
                        onDelete = { onDeleteClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalExpenseRow(
    item: PersonalExpense,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            val (icon, iconColor, bgColor) = when (item.category) {
                "Farm Operations" -> Triple(Icons.Default.Agriculture, OrangePrimary, OrangeLight)
                "Household" -> Triple(Icons.Default.Home, Color(0xFF1E88E5), Color(0xFFE3F2FD))
                "Loan & EMI" -> Triple(Icons.Default.AccountBalance, RedError, RedLight)
                "Livestock & Dairy" -> Triple(Icons.Default.Pets, Color(0xFF6D4C41), Color(0xFFEFEBE9))
                "Utilities" -> Triple(Icons.Default.Bolt, Color(0xFFFBC02D), Color(0xFFFFFDE7))
                "Health & Education" -> Triple(Icons.Default.School, Color(0xFF8E24AA), Color(0xFFF3E5F5))
                else -> Triple(Icons.Default.AttachMoney, GreenText, GreenSurface)
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(text = " • ", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = item.date,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (item.isExpense) "-" else "+"}${formatRupees(item.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (item.isExpense) RedError else GreenDark
                )
                Text(
                    text = if (item.isExpense) "खर्च (Outflow)" else "जमा (Inflow)",
                    fontSize = 9.sp,
                    color = if (item.isExpense) RedError.copy(alpha = 0.8f) else GreenDark.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ==========================================
// 4. TAB 2: FINANCIAL TALLY & BALANCE SHEET
// ==========================================

@Composable
private fun FinancialTallyTab(
    cropBudget: Int,
    cropName: String,
    personalOutflow: Int,
    personalInflow: Int,
    marketplaceRevenue: Int,
    netBalance: Int,
    expensesList: List<PersonalExpense>,
    onGoToCalculator: () -> Unit,
    onGoToPersonal: () -> Unit,
    onAddRecord: (Boolean, String) -> Unit
) {
    val totalInflow = marketplaceRevenue + personalInflow
    val totalOutflow = cropBudget + personalOutflow
    val isProfit = netBalance >= 0

    // Group personal expenses by category for breakdown
    val categoryTotals = expensesList.filter { it.isExpense }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tally Card Hero
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isProfit) GreenDark else Color(0xFFC62828)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Net Farm & Family Balance",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "एकूण शिल्लक (नफा / तोटा)",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isProfit) "✅ Profitable (नफ्यात)" else "⚠️ Deficit (खर्च जास्त)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = formatRupees(netBalance),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Earnings (जमा)", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        Text(formatRupees(totalInflow), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Spending (खर्च)", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        Text(formatRupees(totalOutflow), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Two-Column Ledger: Inflows vs Outflows
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Financial Tally Sheet (ताळेबंद पत्रक)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Detailed comparison of all revenues and expenditures",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Inflow items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🟢 Inflows & Receipts (जमा बाजू)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                    TextButton(
                        onClick = { onAddRecord(false, "Farm Income") },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("+ Record Inflow / Sale", fontSize = 11.sp, color = GreenText, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                TallyLedgerRow(label = "Marketplace Crop Sales", amount = marketplaceRevenue, isExpense = false)
                expensesList.filter { !it.isExpense }.forEach { item ->
                    TallyLedgerRow(label = item.title, amount = item.amount, isExpense = false)
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = AppDivider)

                // Section 2: Outflow items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔴 Outflows & Expenditures (खर्च बाजू)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedError
                    )
                    TextButton(
                        onClick = { onAddRecord(true, "Farm Operations") },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("+ Add Expense Entry", fontSize = 11.sp, color = RedError, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                TallyLedgerRow(
                    label = "Crop Production ($cropName)",
                    amount = cropBudget,
                    isExpense = true,
                    onClickAction = onGoToCalculator
                )

                categoryTotals.forEach { (category, amount) ->
                    TallyLedgerRow(
                        label = category,
                        amount = amount,
                        isExpense = true,
                        onClickAction = onGoToPersonal
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp), thickness = 2.dp, color = AppDivider)

                // Summary Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Net Remaining Balance:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = formatRupees(netBalance),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isProfit) GreenDark else RedError
                    )
                }
            }
        }

        // Expense Proportions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Expenditure Breakdown (खर्चाची वर्गवारी)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Crop percentage
                if (totalOutflow > 0) {
                    val cropShare = cropBudget.toFloat() / totalOutflow.toFloat()
                    val cropPercent = (cropShare * 100).toInt()
                    ExpenseCategoryBar(
                        label = "Crop Production ($cropName)",
                        amount = cropBudget,
                        percentage = cropPercent,
                        color = GreenPrimary
                    )

                    categoryTotals.forEach { (cat, amt) ->
                        val catShare = amt.toFloat() / totalOutflow.toFloat()
                        val catPercent = (catShare * 100).toInt()
                        val color = when (cat) {
                            "Farm Operations" -> OrangePrimary
                            "Household" -> Color(0xFF1E88E5)
                            "Loan & EMI" -> RedError
                            "Livestock & Dairy" -> Color(0xFF795548)
                            else -> Color(0xFF9C27B0)
                        }
                        ExpenseCategoryBar(
                            label = cat,
                            amount = amt,
                            percentage = catPercent,
                            color = color
                        )
                    }
                }
            }
        }

        // Agricultural Financial Advisory Note
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GreenSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = GreenDark,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Farmer Advisory (शेती सल्ला)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GreenDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isProfit) {
                            "Your farm finances are running in healthy surplus! Maintaining direct records of tractor diesel and KCC loan interest helps maximize seasonal profit."
                        } else {
                            "Outflows currently exceed inflows. Record your crop mandi harvest sale or consider optimizing pesticide and fertilizer dosages to balance seasonal cashflow."
                        },
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun TallyLedgerRow(
    label: String,
    amount: Int,
    isExpense: Boolean,
    onClickAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (onClickAction != null) Modifier.clickable { onClickAction() } else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isExpense) RedError else GreenDark)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = "${if (isExpense) "-" else "+"}${formatRupees(amount)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isExpense) RedError else GreenDark
        )
    }
}

@Composable
private fun ExpenseCategoryBar(
    label: String,
    amount: Int,
    percentage: Int,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = "${formatRupees(amount)} ($percentage%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (percentage.toFloat() / 100f).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = AppDivider
        )
    }
}

// ==========================================
// 5. ADD EXPENSE / INCOME DIALOG
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    initialIsExpense: Boolean = true,
    initialCategory: String? = null,
    onDismiss: () -> Unit,
    onSave: (PersonalExpense) -> Unit
) {
    var isExpense by remember { mutableStateOf(initialIsExpense) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val expenseCategories = remember {
        listOf(
            "Household",
            "Farm Operations",
            "Loan & EMI",
            "Livestock & Dairy",
            "Utilities",
            "Health & Education",
            "Other Expense"
        )
    }
    val incomeCategories = remember {
        listOf(
            "Farm Income",
            "Dairy & Milk",
            "Govt Subsidy",
            "Equipment Rent",
            "Other Income"
        )
    }

    val currentCategories = if (isExpense) expenseCategories else incomeCategories
    var selectedCategory by remember {
        mutableStateOf(
            if (initialCategory != null && currentCategories.contains(initialCategory)) {
                initialCategory
            } else {
                currentCategories[0]
            }
        )
    }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isExpense) "Record Expense (खर्च नोंदवा)" else "Record Income (जमा नोंदवा)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Outflow / Inflow Toggle
                Row(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isExpense = true
                                selectedCategory = expenseCategories[0]
                            },
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        color = if (isExpense) RedLight else Color(0xFFF5F5F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpense) RedError else AppDivider)
                    ) {
                        Text(
                            text = "🔴 Expense (खर्च)",
                            fontSize = 12.sp,
                            fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal,
                            color = if (isExpense) RedError else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isExpense = false
                                selectedCategory = incomeCategories[0]
                            },
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        color = if (!isExpense) GreenSurface else Color(0xFFF5F5F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (!isExpense) GreenPrimary else AppDivider)
                    ) {
                        Text(
                            text = "🟢 Income (जमा)",
                            fontSize = 12.sp,
                            fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isExpense) GreenDark else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Title / Description (शीर्षक)") },
                    placeholder = { Text(if (isExpense) "e.g. Tractor Diesel, School Fees" else "e.g. Wheat Harvest Sale, Subsidy") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    label = { Text("Amount in Rupees (रक्कम ₹)") },
                    placeholder = { Text("e.g. 5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text(" ₹ ", fontWeight = FontWeight.Bold, color = TextPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category (वर्गवारी)") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        currentCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = RedError,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toIntOrNull() ?: 0
                    if (title.isBlank()) {
                        errorMessage = "Please enter a description"
                    } else if (amt <= 0) {
                        errorMessage = "Please enter a valid amount greater than 0"
                    } else {
                        onSave(
                            PersonalExpense(
                                title = title.trim(),
                                category = selectedCategory,
                                amount = amt,
                                date = "Today",
                                isExpense = isExpense
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Entry", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
