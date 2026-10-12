package com.krushiadhaar.app.management

import android.widget.Toast
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

// Backward-compatibility class
data class InventoryItem(
    val name: String,
    val status: String,
    val statusOk: Boolean,
    val emoji: String
)

private fun formatRupee(amount: Int): String {
    val isNegative = amount < 0
    val absVal = kotlin.math.abs(amount)
    val s = absVal.toString()
    val formatted = if (s.length <= 3) {
        s
    } else {
        val last3 = s.substring(s.length - 3)
        val rest = s.substring(0, s.length - 3)
        val groups = mutableListOf<String>()
        var i = rest.length
        while (i > 0) {
            val start = kotlin.math.max(0, i - 2)
            groups.add(0, rest.substring(start, i))
            i -= 2
        }
        groups.joinToString(",") + "," + last3
    }
    val sign = if (isNegative) "-" else ""
    return "$sign\u20B9$formatted"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropManagementScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "All" to "सर्व साहित्य",
        "Harvested Produce" to "🌾 धान्य/उत्पन्न",
        "Fertilizers" to "🧪 खते",
        "Seeds" to "🌱 बियाणे",
        "Pesticides" to "🛡️ कीटकनाशके",
        "Machinery & Tools" to "🚜 यंत्रे व अवजारे",
        "Animal Feed" to "🐄 चारा व पशुखाद्य"
    )

    val inventoryList = GlobalMockData.inventoryItems

    // Filter items
    val filteredItems = remember(inventoryList.size, searchQuery, selectedCategoryFilter) {
        inventoryList.filter { item ->
            val matchesCategory = when (selectedCategoryFilter) {
                "All" -> true
                else -> item.category.equals(selectedCategoryFilter, ignoreCase = true)
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.brandOrVariety.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.storageLocation.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    // High level stats
    val totalItemsCount = inventoryList.size
    val totalStockValue = inventoryList.sumOf { (it.quantity * it.pricePerUnit).toInt() }
    val alertItemsCount = inventoryList.count { it.status == "Low Stock" || it.status == "Out of Stock" || it.status == "Needs Service" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Farm Inventory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "कृषी भांडार व साठा व्यवस्थापन",
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
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Item", tint = GreenText)
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
                text = { Text("Add Item (नवीन नोंद)", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // KPI Overview Banner
            InventorySummaryCards(
                totalCount = totalItemsCount,
                totalWorth = totalStockValue,
                alertCount = alertItemsCount
            )

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
                    placeholder = { Text("Search by name, brand, or location...", fontSize = 13.sp) },
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

                // Category Chips
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

            Divider(color = AppDivider)

            // Items List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📦", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No inventory items found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the button below to add stock or produce.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add First Item")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        AgriInventoryCard(
                            item = item,
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
                                Toast.makeText(context, "${item.name} removed from inventory", Toast.LENGTH_SHORT).show()
                            },
                            onSellOnMandi = {
                                // Add listing to marketplace
                                val qtyInt = item.quantity.toInt().coerceAtLeast(1)
                                GlobalMockData.marketplaceListings.add(
                                    0,
                                    CropListing(
                                        name = item.name.substringBefore(" ("),
                                        farmer = "${GlobalMockData.farmerName.value} (You)",
                                        location = GlobalMockData.userLocation.value,
                                        quantityTons = qtyInt,
                                        pricePerTon = item.pricePerUnit * 10, // approximate ton conversion
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
                        Spacer(modifier = Modifier.height(64.dp)) // padding for FAB
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAgriInventoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                GlobalMockData.addInventoryItem(newItem)
                showAddDialog = false
                Toast.makeText(context, "Added ${newItem.name} to inventory!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// 1. TOP SUMMARY CARDS
// ==========================================

@Composable
private fun InventorySummaryCards(
    totalCount: Int,
    totalWorth: Int,
    alertCount: Int
) {
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
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stat 1: Total Items
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = "Total Items", fontSize = 11.sp, color = TextSecondary)
                Text(
                    text = "$totalCount Items",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(text = "एकूण साहित्य", fontSize = 10.sp, color = TextMuted)
            }

            Divider(modifier = Modifier.height(36.dp).width(1.dp), color = AppDivider)

            // Stat 2: Total Stock Value
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Stock Worth", fontSize = 11.sp, color = TextSecondary)
                Text(
                    text = formatRupee(totalWorth),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenDark
                )
                Text(text = "एकूण साठा मूल्य", fontSize = 10.sp, color = TextMuted)
            }

            Divider(modifier = Modifier.height(36.dp).width(1.dp), color = AppDivider)

            // Stat 3: Alerts
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Stock Alerts", fontSize = 11.sp, color = TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (alertCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (alertCount > 0) RedError else GreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (alertCount == 0) "All Good" else "$alertCount Need Action",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alertCount > 0) RedError else GreenDark
                    )
                }
                Text(text = "कमी साठा / दुरुस्ती", fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

// ==========================================
// 2. INVENTORY ITEM CARD
// ==========================================

@Composable
private fun AgriInventoryCard(
    item: AgriInventoryItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit,
    onSellOnMandi: () -> Unit
) {
    val totalValue = (item.quantity * item.pricePerUnit).toInt()
    val isProduce = item.category.contains("Harvested", ignoreCase = true) || item.category.contains("Produce", ignoreCase = true)

    val (badgeBg, badgeText, statusColor) = when (item.status) {
        "In Stock", "Operational" -> Triple(GreenSurface, item.status, GreenDark)
        "Low Stock" -> Triple(Color(0xFFFFF8E1), "Low Stock (कमी)", Color(0xFFF57F17))
        "Needs Service" -> Triple(Color(0xFFFFEBEE), "Needs Service (दुरुस्ती)", RedError)
        "Out of Stock" -> Triple(Color(0xFFFFEBEE), "Out of Stock (संपले)", RedError)
        else -> Triple(GreenSurface, item.status, GreenDark)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Header (Emoji + Name + Status Badge + Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = 24.sp)
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
                    if (item.brandOrVariety.isNotBlank()) {
                        Text(
                            text = "${item.brandOrVariety} • ${item.category}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = item.category,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = TextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = AppDivider)
            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Metrics Grid (Quantity, Total Value, Location, Expiry)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Block
                Column {
                    Text(text = "Quantity (साठा)", fontSize = 11.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val qtyStr = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()}" else "${item.quantity}"
                        Text(
                            text = "$qtyStr ${item.unit}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }
                }

                // Value Block
                if (item.pricePerUnit > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Worth", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = formatRupee(totalValue),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        Text(
                            text = "@ \u20B9${item.pricePerUnit}/${item.unit.singular()}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Location Block
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Location (ठिकाण)", fontSize = 11.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = item.storageLocation,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                    if (item.expiryOrServiceDate.isNotBlank()) {
                        Text(
                            text = item.expiryOrServiceDate,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Notes if any
            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF8F9FA))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.notes,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Action Buttons (Quick adjust quantity + Optional Mandi sell)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock stepper controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Quick Adjust: ", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    FilledTonalIconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(30.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFEEEEEE))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    FilledTonalIconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(30.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = GreenSurface)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = GreenDark, modifier = Modifier.size(16.dp))
                    }
                }

                // If harvested produce, show "Sell on Mandi"
                if (isProduce) {
                    Button(
                        onClick = onSellOnMandi,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("🌾 List on Mandi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun String.singular(): String {
    return when {
        endsWith("Quintals", ignoreCase = true) -> "Qtl"
        endsWith("Bags", ignoreCase = true) -> "Bag"
        endsWith("Liters", ignoreCase = true) -> "L"
        endsWith("Units", ignoreCase = true) -> "Unit"
        endsWith("Tons", ignoreCase = true) -> "Ton"
        else -> this
    }
}

// ==========================================
// 3. ADD INVENTORY ITEM DIALOG
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAgriInventoryDialog(
    onDismiss: () -> Unit,
    onSave: (AgriInventoryItem) -> Unit
) {
    var category by remember { mutableStateOf("Harvested Produce") }
    var name by remember { mutableStateOf("") }
    var brandOrVariety by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("Quintals") }
    var priceText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Farm Godown") }
    var status by remember { mutableStateOf("In Stock") }
    var dateInfo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categoryList = listOf(
        "Harvested Produce" to "🌾",
        "Fertilizers" to "🧪",
        "Seeds" to "🌱",
        "Pesticides" to "🛡️",
        "Machinery & Tools" to "🚜",
        "Animal Feed" to "🐄"
    )

    val unitsForProduce = listOf("Quintals", "Bags", "kg", "Tons")
    val unitsForFertilizers = listOf("Bags", "kg", "Liters")
    val unitsForSeeds = listOf("kg", "Bags", "Packets")
    val unitsForMachinery = listOf("Units", "Sets")
    val unitsForFeed = listOf("Bags", "Quintals", "kg")

    val activeUnits = when (category) {
        "Harvested Produce" -> unitsForProduce
        "Fertilizers" -> unitsForFertilizers
        "Seeds" -> unitsForSeeds
        "Machinery & Tools" -> unitsForMachinery
        "Animal Feed" -> unitsForFeed
        else -> listOf("Bags", "kg", "Liters", "Units")
    }

    // Preset quick suggestions
    val quickPresets = listOf(
        Triple("DAP 18:46:0", "Fertilizers", "1350"),
        Triple("Urea (Neem Coated)", "Fertilizers", "266"),
        Triple("Soyabean (JS-335)", "Harvested Produce", "4600"),
        Triple("Wheat (Lokwan)", "Harvested Produce", "2600"),
        Triple("MOP Potash", "Fertilizers", "1700"),
        Triple("Cotton Bales", "Harvested Produce", "7200"),
        Triple("Chana / Gram Seed", "Seeds", "95"),
        Triple("Sprayer Pump", "Machinery & Tools", "0"),
        Triple("Cattle Feed Fodder", "Animal Feed", "1250")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = GreenDark)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Add Stock Item (नवीन साठा नोंद)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Fill inventory details & current stock", fontSize = 11.sp, color = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Suggestion Chips
                Text("Quick Suggestions (द्रुत पर्याय):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickPresets) { (presetName, presetCat, presetPrice) ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    name = presetName
                                    category = presetCat
                                    priceText = presetPrice
                                    unit = when (presetCat) {
                                        "Fertilizers" -> "Bags"
                                        "Seeds" -> "kg"
                                        "Machinery & Tools" -> "Units"
                                        else -> "Quintals"
                                    }
                                    if (presetCat == "Machinery & Tools") {
                                        status = "Operational"
                                    }
                                },
                            color = GreenSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = presetName,
                                fontSize = 11.sp,
                                color = GreenDark,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Category selector
                Text("Category (वर्गवारी):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categoryList) { (cat, emoji) ->
                        val isSelected = category == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    category = cat
                                    if (unit !in activeUnits) unit = activeUnits.first()
                                },
                            color = if (isSelected) GreenDark else Color(0xFFEEEEEE)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Item Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name (वस्तू / उत्पादनाचे नाव)") },
                    placeholder = { Text("e.g. IFFCO DAP / Soyabean") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Brand / Variety
                OutlinedTextField(
                    value = brandOrVariety,
                    onValueChange = { brandOrVariety = it },
                    label = { Text("Brand / Variety (कंपनी किंवा वाण)") },
                    placeholder = { Text("e.g. Mahyco JS 335 / Bayer / IFFCO") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quantity & Unit Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity (प्रमाण)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )

                    // Unit selector chips
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unit (एकक):", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            activeUnits.take(3).forEach { u ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { unit = u },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (unit == u) GreenSurface else Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (unit == u) GreenPrimary else AppDivider)
                                ) {
                                    Text(
                                        text = u,
                                        fontSize = 10.sp,
                                        fontWeight = if (unit == u) FontWeight.Bold else FontWeight.Normal,
                                        color = if (unit == u) GreenDark else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Price Per Unit
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Rate per Unit in \u20B9 (दर प्रति एकक)") },
                    leadingIcon = { Text("\u20B9", fontWeight = FontWeight.Bold, color = GreenDark) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Live total worth preview
                val q = quantityText.toDoubleOrNull() ?: 0.0
                val p = priceText.toIntOrNull() ?: 0
                if (q > 0 && p > 0) {
                    val calcTotal = (q * p).toInt()
                    Surface(
                        color = GreenSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💰 Total Stock Value: ${formatRupee(calcTotal)}",
                            color = GreenDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Storage Location picker
                Text("Storage Location (साठवण ठिकाण):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Farm Godown", "Home Storeroom", "Field Shed", "Open Yard").forEach { loc ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { location = loc },
                            shape = RoundedCornerShape(8.dp),
                            color = if (location == loc) GreenDark else Color(0xFFEEEEEE)
                        ) {
                            Text(
                                text = loc.replace(" ", "\n"),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                color = if (location == loc) Color.White else TextPrimary,
                                fontWeight = if (location == loc) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                // Status & Expiry Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateInfo,
                        onValueChange = { dateInfo = it },
                        label = { Text("Exp / Service Date") },
                        placeholder = { Text("e.g. Dec 2026") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Details (टीप)") },
                    placeholder = { Text("e.g. Cleaned & packed, 50kg bags") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val qty = quantityText.toDoubleOrNull() ?: 1.0
                        val rate = priceText.toIntOrNull() ?: 0
                        val emoji = when (category) {
                            "Harvested Produce" -> "🌾"
                            "Fertilizers" -> "🧪"
                            "Seeds" -> "🌱"
                            "Pesticides" -> "🛡️"
                            "Machinery & Tools" -> "🚜"
                            "Animal Feed" -> "🐄"
                            else -> "📦"
                        }
                        onSave(
                            AgriInventoryItem(
                                name = name.trim(),
                                category = category,
                                quantity = qty,
                                unit = unit,
                                pricePerUnit = rate,
                                storageLocation = location,
                                status = status,
                                brandOrVariety = brandOrVariety.trim(),
                                expiryOrServiceDate = dateInfo.trim(),
                                notes = notes.trim(),
                                emoji = emoji
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                enabled = name.isNotBlank()
            ) {
                Text("Save Item (साठवा)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
