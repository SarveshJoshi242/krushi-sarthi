package com.krushiadhaar.app.management

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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

// Helper for clean Indian Rupee Currency string
fun formatRupees(amount: Number, includeSymbol: Boolean = true): String {
    val num = amount.toLong()
    val isNeg = num < 0
    val absVal = kotlin.math.abs(num)
    val s = absVal.toString()
    val formatted = if (s.length <= 3) {
        s
    } else {
        val last3 = s.takeLast(3)
        val rest = s.dropLast(3)
        val buffer = StringBuilder()
        for (i in rest.indices) {
            buffer.append(rest[i])
            val remaining = rest.length - 1 - i
            if (remaining > 0 && remaining % 2 == 0) {
                buffer.append(",")
            }
        }
        buffer.append(",").append(last3).toString()
    }
    val sign = if (isNeg) "-" else ""
    return if (includeSymbol) "$sign₹$formatted" else "$sign$formatted"
}

data class SimpleCropRate(
    val id: String,
    val nameEn: String,
    val nameMr: String,
    val nameHi: String,
    val emoji: String,
    val baseCostPerAcre: Int,
    val seedShare: Float = 0.15f,
    val fertShare: Float = 0.40f,
    val pestShare: Float = 0.10f,
    val laborShare: Float = 0.35f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCalculatorScreen(
    onNavigateBack: () -> Unit,
    initialTab: Int = 0
) {
    val context = LocalContext.current
    val currentLang = GlobalMockData.userLanguage.value

    val isMr = currentLang.contains("Marathi", true) || currentLang.contains("मराठी", true)
    val isHi = currentLang.contains("Hindi", true) || currentLang.contains("हिंदी", true)

    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabTitles = remember(currentLang) {
        if (isMr) listOf("💰 पाकीट व पासबुक", "🌾 पीक खर्च अंदाज", "📒 जमा-खर्च वही", "📊 आर्थिक ताळेबंद")
        else if (isHi) listOf("💰 बटुआ व पासबुक", "🌾 फसल लागत अनुमान", "📒 व्यक्तिगत बहीखाता", "📊 वित्तीय सारांश")
        else listOf("💰 Wallet & Passbook", "🌾 Crop Cost", "📒 Farm Ledger", "📊 Financial Tally")
    }

    val crops = remember {
        listOf(
            SimpleCropRate("sugarcane", "Sugarcane", "ऊस", "गन्ना", "🎋", 28000, 0.18f, 0.42f, 0.10f, 0.30f),
            SimpleCropRate("cotton", "Cotton", "कापूस", "कपास", "🪴", 14000, 0.12f, 0.38f, 0.20f, 0.30f),
            SimpleCropRate("soybean", "Soybean", "सोयाबीन", "सोयाबीन", "🌱", 9200, 0.20f, 0.35f, 0.15f, 0.30f),
            SimpleCropRate("wheat", "Wheat", "गहू", "गेहूं", "🌾", 8000, 0.15f, 0.40f, 0.10f, 0.35f),
            SimpleCropRate("rice", "Rice", "भात", "धान", "🌾", 11000, 0.15f, 0.35f, 0.15f, 0.35f),
            SimpleCropRate("onion", "Onion", "कांदा", "प्याज", "🧅", 16000, 0.15f, 0.35f, 0.15f, 0.35f),
            SimpleCropRate("maize", "Maize", "मका", "मक्का", "🌽", 7500, 0.15f, 0.40f, 0.10f, 0.35f),
            SimpleCropRate("potato", "Potato", "बटाटा", "आलू", "🥔", 18000, 0.25f, 0.35f, 0.15f, 0.25f),
            SimpleCropRate("tomato", "Tomato", "टोमॅटो", "टमाटर", "🍅", 22000, 0.15f, 0.35f, 0.20f, 0.30f),
            SimpleCropRate("brinjal", "Brinjal", "वांगी", "बैंगन", "🍆", 15000, 0.12f, 0.38f, 0.20f, 0.30f),
            SimpleCropRate("groundnut", "Groundnut", "भुईमूग", "मूंगफली", "🥜", 12000, 0.25f, 0.35f, 0.10f, 0.30f),
            SimpleCropRate("pumpkin", "Pumpkin", "भोपळा", "कद्दू", "🎃", 10000, 0.15f, 0.35f, 0.15f, 0.35f),
            SimpleCropRate("bitter_gourd", "Bitter Gourd", "कारले", "करेला", "🥒", 14000, 0.15f, 0.35f, 0.18f, 0.32f),
            SimpleCropRate("ridge_gourd", "Ridge Gourd", "दोडका", "तोरई", "🥬", 12500, 0.15f, 0.35f, 0.18f, 0.32f),
            SimpleCropRate("chilli", "Chilli", "मिरची", "मिर्च", "🌶️", 19000, 0.15f, 0.35f, 0.20f, 0.30f),
            SimpleCropRate("other", "Other Crop", "इतर पीक", "अन्य फसल", "🌿", 12000, 0.15f, 0.35f, 0.15f, 0.35f)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isMr) "खर्च व पाकीट व्यवस्थापन"
                            else if (isHi) "खर्च एवं बटुआ प्रबंधन"
                            else "Expenses & Wallet Hub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (selectedTab == 0) "Wallet Passbook & Digital Balance"
                            else if (selectedTab == 1) "Component-wise Crop Budget Estimator"
                            else if (selectedTab == 2) "Daily Farm Cashbook & Receipts"
                            else "P&L Summary & Harvest Margin",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenPrimary,
                edgePadding = 12.dp
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(modifier = Modifier.widthIn(max = 680.dp).fillMaxSize()) {
                    when (selectedTab) {
                        0 -> WalletPassbookTab(isMr = isMr, isHi = isHi)
                        1 -> CropCostCalculatorTab(crops = crops, isMr = isMr, isHi = isHi)
                        2 -> PersonalLedgerTab(isMr = isMr, isHi = isHi)
                        3 -> FinancialTallyTab(isMr = isMr, isHi = isHi)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 0: WALLET & PASSBOOK (MERGED)
// ==========================================

@Composable
private fun WalletPassbookTab(
    isMr: Boolean,
    isHi: Boolean
) {
    val context = LocalContext.current
    val balance = GlobalMockData.walletBalance.value
    val transactions = GlobalMockData.transactions
    val cropRevenue = GlobalMockData.cropYieldRevenue.value

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isMr) "एकूण पाकीट शिल्लक" else if (isHi) "कुल बटुआ शेष" else "Total Wallet Balance",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatRupees(balance),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Interactive Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showDepositDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMr) "पैसे जोडा" else if (isHi) "पैसे जोड़ें" else "Deposit (+)",
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { showWithdrawDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMr) "पैसे काढा" else if (isHi) "निकासी करें" else "Withdraw (-)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Secondary Financial Snapshot
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isMr) "पीक विक्री महसूल" else if (isHi) "उपज बिक्री राजस्व" else "Crop Sales Turnover",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = formatRupees(cropRevenue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                }
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = AppDivider)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isMr) "केवायसी स्थिती" else if (isHi) "केवाईसी स्थिति" else "KYC Status",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aadhaar Linked",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    }
                }
            }
        }

        // Recent Passbook Transactions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isMr) "पासबुक व्यवहार (Transactions)"
                else if (isHi) "पासबुक लेन-देन (Transactions)"
                else "Recent Passbook Transactions",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = "${transactions.size} Records",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (transactions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    transactions.forEachIndexed { index, tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (tx.isCredit) GreenSurface else Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (tx.isCredit) "+" else "-",
                                    color = if (tx.isCredit) GreenDark else Color(0xFFE53935),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.description,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (tx.isCredit) "Direct Credit • Success" else "Debit Transfer • Success",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = (if (tx.isCredit) "+ " else "- ") + formatRupees(tx.amount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (tx.isCredit) GreenDark else Color(0xFFE53935)
                            )
                        }
                        if (index < transactions.size - 1) {
                            Divider(color = Color(0xFFF5F5F5))
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💳", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Transactions Yet", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text("Tap 'Deposit' to credit your farm wallet", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        var depositAmount by remember { mutableStateOf("") }
        var depositDesc by remember { mutableStateOf("Harvest Sales / Farm Income") }

        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text(if (isMr) "खात्यात पैसे जोडा" else if (isHi) "खाते में राशि जोड़ें" else "Deposit Money into Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = depositDesc,
                        onValueChange = { depositDesc = it },
                        label = { Text("Source / Description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depositAmount.toIntOrNull() ?: 0
                        if (amt > 0) {
                            GlobalMockData.depositToWallet(amt, depositDesc.trim())
                            Toast.makeText(context, "Added ₹$amt to Wallet!", Toast.LENGTH_SHORT).show()
                            showDepositDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Confirm Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        var withdrawAmount by remember { mutableStateOf("") }
        var withdrawDesc by remember { mutableStateOf("Direct Bank Withdrawal") }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text(if (isMr) "रक्कम काढा (Withdraw)" else if (isHi) "निकासी करें (Withdraw)" else "Withdraw from Wallet", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Available Balance: ${formatRupees(balance)}", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("Withdraw Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = withdrawDesc,
                        onValueChange = { withdrawDesc = it },
                        label = { Text("Bank / Purpose") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmount.toIntOrNull() ?: 0
                        if (amt > 0) {
                            val success = GlobalMockData.withdrawFromWallet(amt, withdrawDesc.trim())
                            if (success) {
                                Toast.makeText(context, "Withdrawn ₹$amt successfully!", Toast.LENGTH_SHORT).show()
                                showWithdrawDialog = false
                            } else {
                                Toast.makeText(context, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Confirm Withdrawal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// TAB 1: CROP COST ESTIMATOR
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropCostCalculatorTab(
    crops: List<SimpleCropRate>,
    isMr: Boolean,
    isHi: Boolean
) {
    val context = LocalContext.current
    val savedCrop = GlobalMockData.farmerCrop.value
    var selectedCrop by remember {
        mutableStateOf(crops.find { it.nameEn.equals(savedCrop, true) || it.nameMr.equals(savedCrop, true) } ?: crops[0])
    }
    var cropExpanded by remember { mutableStateOf(false) }

    var farmAreaText by remember { mutableStateOf(GlobalMockData.farmerFarmArea.value.ifBlank { "5" }) }

    // Resource selection checkboxes
    var includeSeeds by remember { mutableStateOf(true) }
    var includeFertilizer by remember { mutableStateOf(true) }
    var includePesticides by remember { mutableStateOf(true) }
    var includeLabor by remember { mutableStateOf(true) }
    var includeIrrigation by remember { mutableStateOf(true) }
    var includeStorage by remember { mutableStateOf(true) }
    var includeOther by remember { mutableStateOf(false) }

    var otherComponentName by remember { mutableStateOf("Borewell / Transport") }
    var otherComponentAmount by remember { mutableStateOf("3000") }

    // Calculated values
    val acres = farmAreaText.toDoubleOrNull() ?: 5.0
    var calculatedTotal by remember { mutableIntStateOf(GlobalMockData.estimatedCropCost.intValue) }

    fun recalculate() {
        val baseTotal = selectedCrop.baseCostPerAcre * acres
        var shareSum = 0f
        if (includeSeeds) shareSum += 0.15f
        if (includeFertilizer) shareSum += 0.35f
        if (includePesticides) shareSum += 0.10f
        if (includeLabor) shareSum += 0.25f
        if (includeIrrigation) shareSum += 0.10f
        if (includeStorage) shareSum += 0.05f

        val otherCost = if (includeOther) (otherComponentAmount.toIntOrNull() ?: 0) else 0
        val finalCost = ((baseTotal * shareSum) + otherCost).toInt().coerceAtLeast(0)
        calculatedTotal = finalCost
        GlobalMockData.updateCropEstimate(finalCost, selectedCrop.nameEn, farmAreaText)
    }

    LaunchedEffect(
        selectedCrop, acres, includeSeeds, includeFertilizer,
        includePesticides, includeLabor, includeIrrigation, includeStorage,
        includeOther, otherComponentAmount
    ) {
        recalculate()
    }

    val localizedCropName = if (isMr) selectedCrop.nameMr else if (isHi) selectedCrop.nameHi else selectedCrop.nameEn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isMr) "पीक निवडा" else if (isHi) "फसल चुनें" else "Select Crop Type",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = cropExpanded,
                    onExpandedChange = { cropExpanded = !cropExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${selectedCrop.emoji} $localizedCropName",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cropExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = cropExpanded,
                        onDismissRequest = { cropExpanded = false }
                    ) {
                        crops.forEach { crop ->
                            val cName = if (isMr) crop.nameMr else if (isHi) crop.nameHi else crop.nameEn
                            DropdownMenuItem(
                                text = { Text("${crop.emoji} $cName", fontSize = 14.sp) },
                                onClick = {
                                    selectedCrop = crop
                                    cropExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isMr) "क्षेत्र (एकर मध्ये)" else if (isHi) "रकबा (एकड़ में)" else "Land Area (in Acres)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = farmAreaText,
                    onValueChange = { farmAreaText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    trailingIcon = { Text("Acres", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(end = 12.dp)) }
                )
            }
        }

        // Component Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isMr) "खर्च घटक निवडा" else if (isHi) "लागत घटक चुनें" else "Cost Components to Include",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth().clickable { includeSeeds = !includeSeeds }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeSeeds, onCheckedChange = { includeSeeds = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seeds & Germination (15%)", fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth().clickable { includeFertilizer = !includeFertilizer }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeFertilizer, onCheckedChange = { includeFertilizer = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chemical & Bio Fertilizers (35%)", fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth().clickable { includePesticides = !includePesticides }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includePesticides, onCheckedChange = { includePesticides = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crop Protection & Pesticides (10%)", fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth().clickable { includeLabor = !includeLabor }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeLabor, onCheckedChange = { includeLabor = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tractor, Fuel & Field Labor (25%)", fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth().clickable { includeIrrigation = !includeIrrigation }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeIrrigation, onCheckedChange = { includeIrrigation = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Irrigation & Power Electricity (10%)", fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth().clickable { includeStorage = !includeStorage }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeStorage, onCheckedChange = { includeStorage = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post-Harvest Packaging & Storage (5%)", fontSize = 13.sp)
                }
                
                // Other Option
                Row(modifier = Modifier.fillMaxWidth().clickable { includeOther = !includeOther }, verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeOther, onCheckedChange = { includeOther = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Other (Custom Farm Expenses)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GreenDark)
                }

                if (includeOther) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FBE7), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = otherComponentName,
                            onValueChange = { otherComponentName = it },
                            label = { Text("Expense Description") },
                            placeholder = { Text("e.g. Fencing / Transport / Borewell") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = otherComponentAmount,
                            onValueChange = { otherComponentAmount = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Custom Amount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Total Cost Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenSurface),
            border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isMr) "अंदाजे एकूण मशागत खर्च" else if (isHi) "अनुमानित कुल कृषि लागत" else "Total Estimated Cultivation Cost",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatRupees(calculatedTotal),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenDark
                )
                Text(
                    text = "For $farmAreaText Acres of ${selectedCrop.emoji} $localizedCropName",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        GlobalMockData.updateCropEstimate(calculatedTotal, selectedCrop.nameEn, farmAreaText)
                        Toast.makeText(context, "Saved to Farm Budget!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save to Farm Budget", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// TAB 2: DAILY FARM LEDGER (PERSONAL CASHBOOK)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalLedgerTab(
    isMr: Boolean,
    isHi: Boolean
) {
    val expenses = GlobalMockData.personalExpenses
    var showAddDialog by remember { mutableStateOf(false) }

    val totalIncome = expenses.filter { !it.isExpense }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.isExpense }.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = if (isMr) "एकूण जमा" else if (isHi) "कुल आय" else "Total Income", fontSize = 11.sp, color = TextSecondary)
                    Text(text = formatRupees(totalIncome), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                }
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = AppDivider)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (isMr) "एकूण खर्च" else if (isHi) "कुल व्यय" else "Total Expense", fontSize = 11.sp, color = TextSecondary)
                    Text(text = formatRupees(totalExpense), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RedError)
                }
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = AppDivider)
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = if (isMr) "निव्वळ शिल्लक" else if (isHi) "शुद्ध बचत" else "Net Balance", fontSize = 11.sp, color = TextSecondary)
                    Text(text = formatRupees(netBalance), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (netBalance >= 0) GreenDark else RedError)
                }
            }
        }

        // Add Transaction Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isMr) "नोंदी" else if (isHi) "हालिया लेन-देन" else "Recent Ledger Entries",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isMr) "नोंद जोडा" else if (isHi) "प्रविष्टि जोड़ें" else "Add Entry", fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expenses) { pe ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (!pe.isExpense) GreenSurface else Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (!pe.isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (!pe.isExpense) GreenDark else RedError,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pe.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text("${pe.category} • ${pe.date}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Text(
                            text = (if (!pe.isExpense) "+ " else "- ") + formatRupees(pe.amount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (!pe.isExpense) GreenDark else RedError
                        )
                        IconButton(onClick = { GlobalMockData.removePersonalExpense(pe.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            isMr = isMr,
            isHi = isHi,
            onDismiss = { showAddDialog = false },
            onSave = { newEntry ->
                GlobalMockData.addPersonalExpense(newEntry)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddExpenseDialog(
    isMr: Boolean,
    isHi: Boolean,
    onDismiss: () -> Unit,
    onSave: (PersonalExpense) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var category by remember { mutableStateOf("Farm Operations") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isMr) "नवीन नोंद" else if (isHi) "नया लेन-देन" else "New Entry", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { isExpense = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isExpense) RedError else Color(0xFFF1F3F4)),
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isMr) "खर्च" else if (isHi) "व्यय" else "Expense", color = if (isExpense) Color.White else TextPrimary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { isExpense = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isExpense) GreenPrimary else Color(0xFFF1F3F4)),
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isMr) "जमा" else if (isHi) "आय" else "Income", color = if (!isExpense) Color.White else TextPrimary, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isMr) "तपशील" else if (isHi) "विवरण" else "Description") },
                    placeholder = { Text("e.g. Diesel for tractor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isMr) "रक्कम (₹)" else if (isHi) "राशि (₹)" else "Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toIntOrNull() ?: 0
                    if (title.isNotBlank() && amt > 0) {
                        onSave(
                            PersonalExpense(
                                title = title.trim(),
                                category = category,
                                amount = amt,
                                date = "Today",
                                isExpense = isExpense
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(if (isMr) "जतन करा" else if (isHi) "सहेजें" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMr) "रद्द करा" else if (isHi) "रद्द करें" else "Cancel")
            }
        }
    )
}

// ==========================================
// TAB 3: FINANCIAL TALLY & INSIGHTS
// ==========================================

@Composable
private fun FinancialTallyTab(
    isMr: Boolean,
    isHi: Boolean
) {
    val estimatedCropCost = GlobalMockData.estimatedCropCost.intValue
    val revenue = GlobalMockData.cropYieldRevenue.value
    val wallet = GlobalMockData.walletBalance.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tally Card 1: Crop Yield vs Cultivation Cost
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isMr) "शेती नफा-तोटा ताळेबंद" else if (isHi) "कृषि लाभ-हानि सारांश" else "Farm Profitability Tally",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(if (isMr) "अंदाजे खर्च" else if (isHi) "अनुमानित लागत" else "Estimated Cost", fontSize = 11.sp, color = TextSecondary)
                        Text(formatRupees(estimatedCropCost), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RedError)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (isMr) "उत्पन्न विक्री मूल्य" else if (isHi) "उपज बिक्री आय" else "Harvest Revenue", fontSize = 11.sp, color = TextSecondary)
                        Text(formatRupees(revenue), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = AppDivider)
                Spacer(modifier = Modifier.height(10.dp))

                val profit = revenue - estimatedCropCost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMr) "अपेक्षित नफा/तोटा:" else if (isHi) "शुद्ध लाभ/हानि:" else "Net Farm Margin:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = (if (profit >= 0) "+ " else "") + formatRupees(profit),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (profit >= 0) GreenDark else RedError
                    )
                }
            }
        }

        // Tally Card 2: Wallet & Working Capital
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isMr) "खेळते भांडवल व पाकीट" else if (isHi) "कार्यशील पूंजी एवं बटुआ" else "Working Capital & Wallet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isMr) "उपलब्ध शिल्लक:" else if (isHi) "उपलब्ध शेष:" else "Available Balance:", fontSize = 13.sp, color = TextSecondary)
                    Text(formatRupees(wallet), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenDark)
                }
            }
        }
    }
}
