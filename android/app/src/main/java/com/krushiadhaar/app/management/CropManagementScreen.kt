package com.krushiadhaar.app.management

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.krushiadhaar.app.GlobalMockData.AgriInventoryItem
import com.krushiadhaar.app.marketplace.CropListing
import com.krushiadhaar.app.ui.theme.*

private fun formatCurrency(amount: Number): String {
    val num = amount.toLong()
    val isNeg = num < 0
    val absNum = kotlin.math.abs(num)
    val s = absNum.toString()
    val formatted = if (s.length <= 3) {
        s
    } else {
        val last3 = s.takeLast(3)
        val rest = s.dropLast(3)
        val sb = StringBuilder()
        for (i in rest.indices) {
            sb.append(rest[i])
            val rem = rest.length - 1 - i
            if (rem > 0 && rem % 2 == 0) sb.append(",")
        }
        sb.append(",").append(last3).toString()
    }
    val sign = if (isNeg) "-" else ""
    return "$sign₹$formatted"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropManagementScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val currentLang = GlobalMockData.userLanguage.value

    val isMr = currentLang.contains("Marathi", true) || currentLang.contains("मराठी", true)
    val isHi = currentLang.contains("Hindi", true) || currentLang.contains("हिंदी", true)

    // Localized clean single-language strings
    val screenTitle = if (isMr) "कृषी भांडार" else if (isHi) "कृषि भंडार" else "Farm Inventory"
    val screenSubtitle = if (isMr) "साठा व अवजारे व्यवस्थापन" else if (isHi) "भंडारण एवं उपकरण प्रबंधन" else "Stock & Equipment Management"
    val totalItemsLabel = if (isMr) "एकूण वस्तू" else if (isHi) "कुल वस्तुएं" else "Total Items"
    val stockWorthLabel = if (isMr) "साठा मूल्य" else if (isHi) "कुल मूल्य" else "Stock Worth"
    val stockAlertsLabel = if (isMr) "इशारे" else if (isHi) "अलर्ट" else "Stock Alerts"
    val needActionText = if (isMr) "लक्ष द्या" else if (isHi) "कार्रवाई आवश्यक" else "Need Action"
    val searchPlaceholder = if (isMr) "नाव किंवा स्थानानुसार शोधा..." else if (isHi) "नाम या स्थान से खोजें..." else "Search by name, brand, location..."
    val addItemText = if (isMr) "साहित्य जोडा" else if (isHi) "सामग्री जोड़ें" else "Add Item"
    val sellMarketplaceText = if (isMr) "मार्केटमध्ये विका" else if (isHi) "मार्केट में बेचें" else "Sell on Mandi"

    val categories = remember(currentLang) {
        if (isMr) {
            listOf(
                "All" to "सर्व साहित्य",
                "Harvested Produce" to "🌾 धान्य",
                "Fertilizers" to "🧪 खते",
                "Seeds" to "🌱 बियाणे",
                "Pesticides" to "🛡️ कीटकनाशके",
                "Machinery & Tools" to "🚜 यंत्रे",
                "Animal Feed" to "🐄 पशुखाद्य"
            )
        } else if (isHi) {
            listOf(
                "All" to "सभी वस्तुएं",
                "Harvested Produce" to "🌾 उपज",
                "Fertilizers" to "🧪 उर्वरक",
                "Seeds" to "🌱 बीज",
                "Pesticides" to "🛡️ कीटनाशक",
                "Machinery & Tools" to "🚜 कृषि यंत्र",
                "Animal Feed" to "🐄 पशु आहार"
            )
        } else {
            listOf(
                "All" to "All Items",
                "Harvested Produce" to "🌾 Produce",
                "Fertilizers" to "🧪 Fertilizers",
                "Seeds" to "🌱 Seeds",
                "Pesticides" to "🛡️ Pesticides",
                "Machinery & Tools" to "🚜 Machinery",
                "Animal Feed" to "🐄 Animal Feed"
            )
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val inventoryList = GlobalMockData.inventoryItems

    val filteredItems = remember(inventoryList.size, searchQuery, selectedCategoryFilter) {
        inventoryList.filter { item ->
            val matchesCategory = if (selectedCategoryFilter == "All") true else item.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.brandOrVariety.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.storageLocation.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    val totalItemsCount = inventoryList.size
    val totalStockValue = inventoryList.sumOf { (it.quantity * it.pricePerUnit).toInt() }
    val alertItemsCount = inventoryList.count { it.status == "Low Stock" || it.status == "Out of Stock" || it.status == "Needs Service" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = screenTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = screenSubtitle,
                            fontSize = 11.sp,
                            color = GreenDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = addItemText, tint = GreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(addItemText, fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Simplified KPI Overview Banner (3 Clean KPIs)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = totalItemsLabel, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$totalItemsCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Divider(modifier = Modifier.height(32.dp).width(1.dp), color = AppDivider)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stockWorthLabel, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formatCurrency(totalStockValue),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenDark
                        )
                    }

                    Divider(modifier = Modifier.height(32.dp).width(1.dp), color = AppDivider)

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = stockAlertsLabel, fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (alertItemsCount > 0) "$alertItemsCount $needActionText" else "0",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (alertItemsCount > 0) RedError else GreenDark
                        )
                    }
                }
            }

            // Search Bar & Filter Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(searchPlaceholder, fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = AppDivider,
                        focusedBorderColor = GreenPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Clean Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(categories) { (key, label) ->
                        val isSelected = selectedCategoryFilter == key
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedCategoryFilter = key },
                            color = if (isSelected) GreenDark else Color(0xFFF1F3F4),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Inventory Item Cards List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📦", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isMr) "साठा उपलब्ध नाही" else if (isHi) "कोई सामग्री नहीं मिली" else "No items found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isMr) "नवीन नोंद करण्यासाठी खालील बटण दाबा" else if (isHi) "नई सामग्री जोड़ने के लिए नीचे टैप करें" else "Tap below to add items to your inventory",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        SimplifiedInventoryCard(
                            item = item,
                            isMr = isMr,
                            isHi = isHi,
                            sellMarketplaceText = sellMarketplaceText,
                            onIncrement = {
                                val delta = if (item.unit.contains("kg", ignoreCase = true) || item.unit.contains("Liters", ignoreCase = true)) 5.0 else 1.0
                                GlobalMockData.adjustInventoryQuantity(item.id, delta)
                            },
                            onDecrement = {
                                val delta = if (item.unit.contains("kg", ignoreCase = true) || item.unit.contains("Liters", ignoreCase = true)) -5.0 else -1.0
                                GlobalMockData.adjustInventoryQuantity(item.id, delta)
                            },
                            onDelete = {
                                GlobalMockData.removeInventoryItem(item.id)
                                Toast.makeText(context, "${item.name} removed", Toast.LENGTH_SHORT).show()
                            },
                            onSellOnMandi = {
                                val qtyInt = item.quantity.toInt().coerceAtLeast(1)
                                GlobalMockData.marketplaceListings.add(
                                    0,
                                    CropListing(
                                        name = item.name.substringBefore(" ("),
                                        farmer = "${GlobalMockData.farmerName.value} (You)",
                                        location = GlobalMockData.userLocation.value,
                                        quantityTons = qtyInt,
                                        pricePerTon = item.pricePerUnit * 10,
                                        emoji = item.emoji,
                                        emojiBg = Color(0xFFE8F5E9)
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    "🌾 ${item.name} ($qtyInt ${item.unit}) listed on Marketplace for buyers!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        SimplifiedAddInventoryDialog(
            isMr = isMr,
            isHi = isHi,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                GlobalMockData.addInventoryItem(newItem)
                showAddDialog = false
                Toast.makeText(context, "Added ${newItem.name}!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun SimplifiedInventoryCard(
    item: AgriInventoryItem,
    isMr: Boolean,
    isHi: Boolean,
    sellMarketplaceText: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit,
    onSellOnMandi: () -> Unit
) {
    val totalValue = (item.quantity * item.pricePerUnit).toInt()
    val isProduce = item.category.contains("Harvested", ignoreCase = true) || item.category.contains("Produce", ignoreCase = true)

    // Localized status string
    val localizedStatus = when (item.status) {
        "In Stock", "Operational" -> if (isMr) "उपलब्ध" else if (isHi) "उपलब्ध" else "In Stock"
        "Low Stock" -> if (isMr) "कमी साठा" else if (isHi) "कम स्टॉक" else "Low Stock"
        "Needs Service" -> if (isMr) "दुरुस्ती आवश्यक" else if (isHi) "सर्विस आवश्यक" else "Needs Service"
        "Out of Stock" -> if (isMr) "संपले" else if (isHi) "समाप्त" else "Out of Stock"
        else -> item.status
    }

    val (badgeBg, statusColor) = when (item.status) {
        "In Stock", "Operational" -> Pair(GreenSurface, GreenDark)
        "Low Stock" -> Pair(Color(0xFFFFF8E1), Color(0xFFE65100))
        "Needs Service", "Out of Stock" -> Pair(Color(0xFFFFEBEE), RedError)
        else -> Pair(GreenSurface, GreenDark)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Emoji, Name, Status Pill, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (item.brandOrVariety.isNotBlank()) "${item.brandOrVariety} • ${item.category}" else item.category,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = localizedStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp).padding(start = 4.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = AppDivider)
            Spacer(modifier = Modifier.height(10.dp))

            // Clean 3-Metric Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity
                Column {
                    Text(
                        text = if (isMr) "साठा" else if (isHi) "मात्रा" else "Quantity",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    val qtyStr = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()}" else "${item.quantity}"
                    Text(
                        text = "$qtyStr ${item.unit}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Total Worth
                if (item.pricePerUnit > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isMr) "एकूण मूल्य" else if (isHi) "कुल मूल्य" else "Total Worth",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = formatCurrency(totalValue),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    }
                }

                // Storage Location
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isMr) "स्थान" else if (isHi) "स्थान" else "Location",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = item.storageLocation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Quick Adjust & Optional Marketplace Pitch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Adjust
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isMr) "बदल:" else if (isHi) "बदलें:" else "Adjust:",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDecrement, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onIncrement, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = GreenPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                // If Harvested produce, option to list on marketplace
                if (isProduce && item.quantity > 0) {
                    Button(
                        onClick = onSellOnMandi,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(sellMarketplaceText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimplifiedAddInventoryDialog(
    isMr: Boolean,
    isHi: Boolean,
    onDismiss: () -> Unit,
    onSave: (AgriInventoryItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Harvested Produce") }
    var quantityText by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("Quintals") }
    var priceText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Farm Godown") }

    val categoryOptions = listOf(
        "Harvested Produce" to "🌾",
        "Fertilizers" to "🧪",
        "Seeds" to "🌱",
        "Pesticides" to "🛡️",
        "Machinery & Tools" to "🚜",
        "Animal Feed" to "🐄"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isMr) "नवीन साहित्य जोडा" else if (isHi) "नई सामग्री जोड़ें" else "Add New Item",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isMr) "साहित्याचे नाव" else if (isHi) "सामग्री का नाम" else "Item Name") },
                    placeholder = { Text("e.g. Wheat (Lokwan)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Column {
                    Text(
                        text = if (isMr) "प्रवर्ग" else if (isHi) "श्रेणी" else "Category",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categoryOptions.forEach { (cat, emoji) ->
                            val isSel = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) GreenPrimary else Color(0xFFF1F3F4),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = "$emoji $cat",
                                    fontSize = 11.sp,
                                    color = if (isSel) Color.White else TextPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text(if (isMr) "प्रमाण" else if (isHi) "मात्रा" else "Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = { selectedUnit = it },
                        label = { Text(if (isMr) "एकक" else if (isHi) "इकाई" else "Unit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(if (isMr) "दर (प्रति एकक ₹)" else if (isHi) "मूल्य (प्रति इकाई ₹)" else "Price per Unit (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isMr) "साठवणूक स्थान" else if (isHi) "भंडारण स्थान" else "Storage Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val emoji = categoryOptions.find { it.first == selectedCategory }?.second ?: "📦"
                        val qty = quantityText.toDoubleOrNull() ?: 1.0
                        val price = priceText.toIntOrNull() ?: 0
                        onSave(
                            AgriInventoryItem(
                                name = name.trim(),
                                category = selectedCategory,
                                quantity = qty,
                                unit = selectedUnit.trim(),
                                pricePerUnit = price,
                                storageLocation = if (location.isNotBlank()) location.trim() else "Farm Godown",
                                status = "In Stock",
                                emoji = emoji
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
