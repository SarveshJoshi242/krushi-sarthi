package com.krushiadhaar.app.marketplace

import com.krushiadhaar.app.GlobalMockData

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.krushiadhaar.app.ui.theme.*

data class CropListing(
    val name: String,
    val farmer: String,
    val location: String,
    val quantityTons: Int,
    val pricePerTon: Int,
    val emoji: String,
    val emojiBg: Color,
    val imageUri: String? = null,
    val originalQuantity: Double? = null,
    val originalUnit: String? = null,
    val originalPrice: Int? = null,
    val grade: String? = null
)

@Composable
fun CropImageThumbnail(imageUri: String?, emoji: String, emojiBg: Color, size: androidx.compose.ui.unit.Dp = 48.dp) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val bitmap = remember(imageUri) {
        if (!imageUri.isNullOrBlank()) {
            try {
                if (imageUri.startsWith("content://") || imageUri.startsWith("file://")) {
                    val uri = android.net.Uri.parse(imageUri)
                    val input = context.contentResolver.openInputStream(uri)
                    android.graphics.BitmapFactory.decodeStream(input)
                } else {
                    android.graphics.BitmapFactory.decodeFile(imageUri)
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    if (bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Crop Photo",
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(10.dp))
                .background(emojiBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: MarketplaceViewModel? = null,
    isFarmer: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    
    var selectedCrop by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }
    var selectedPrice by remember { mutableStateOf("Any") }
    var selectedQty by remember { mutableStateOf("Any") }

    var showOrderSuccess by remember { mutableStateOf(false) }

    var displayListings = GlobalMockData.marketplaceListings.toList()

    // Apply filters
    displayListings = displayListings.filter { listing ->
        val matchesSearch = searchQuery.isBlank() || listing.name.contains(searchQuery, ignoreCase = true)
        val matchesCrop = selectedCrop == "All" || listing.name.contains(selectedCrop, ignoreCase = true)
        val matchesLoc = selectedLocation == "All" || listing.location.contains(selectedLocation, ignoreCase = true)
        val matchesQty = when (selectedQty) {
            "> 1 Ton" -> listing.quantityTons > 1
            "> 5 Tons" -> listing.quantityTons > 5
            "> 10 Tons" -> listing.quantityTons > 10
            else -> true
        }
        matchesSearch && matchesCrop && matchesLoc && matchesQty
    }
    if (selectedPrice == "Low to High") {
        displayListings = displayListings.sortedBy { it.pricePerTon }
    } else if (selectedPrice == "High to Low") {
        displayListings = displayListings.sortedByDescending { it.pricePerTon }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                
                if (isFarmer) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = GreenPrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Browse Marketplace", fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("My Messages", fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedTab == 0 || !isFarmer) {
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

                    // Filter dropdowns
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DropdownFilter("Crop Type", listOf("All", "Sugarcane", "Wheat", "Cotton", "Rice", "Corn"), selectedCrop) { selectedCrop = it }
                        DropdownFilter("Location", listOf("All", "Pune", "Nagpur", "Kolhapur", "Nashik", "Mumbai"), selectedLocation) { selectedLocation = it }
                        DropdownFilter("Price", listOf("Any", "Low to High", "High to Low"), selectedPrice) { selectedPrice = it }
                        DropdownFilter("Quantity", listOf("Any", "> 1 Ton", "> 5 Tons", "> 10 Tons"), selectedQty) { selectedQty = it }
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
                        items(displayListings) { listing ->
                            ListingCard(
                                listing = listing, 
                                onPlaceOrder = {
                                    com.krushiadhaar.app.GlobalMockData.placeOrder(listing)
                                    showOrderSuccess = true
                                },
                                onNavigateToChat = { 
                                    com.krushiadhaar.app.GlobalMockData.sendNegotiationMessage(listing.farmer, "A Buyer")
                                    onNavigateToChat(listing.farmer) 
                                }
                            )
                        }
                    }
                } else if (selectedTab == 1) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val app = context.applicationContext as com.krushiadhaar.app.KrushiAdhaarApplication
                    val allMessages by app.chatRepository.getAllMessages().collectAsState(initial = emptyList())

                    val conversations = remember(allMessages) {
                        allMessages.groupBy { it.conversationId }.map { (convId, msgs) ->
                            val latest = msgs.maxByOrNull { it.timestamp }
                            val peer = latest?.peerName ?: convId
                            val peerRole = latest?.peerRole ?: "Buyer"
                            val crop = latest?.cropName ?: ""
                            val lastMsg = latest?.text ?: ""
                            val timeStr = latest?.timeFormatted ?: ""
                            Triple(
                                convId,
                                Triple(peer, peerRole, crop),
                                Pair(lastMsg, timeStr)
                            )
                        }
                    }

                    if (conversations.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(GreenSurface, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = GreenPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No messages or negotiations yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "When buyers or traders reach out regarding your pitched crops, direct negotiation conversations will be safely stored and displayed here.",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(conversations) { conv ->
                                val convId = conv.first
                                val peerName = conv.second.first
                                val peerRole = conv.second.second
                                val cropName = conv.second.third
                                val lastMsg = conv.third.first
                                val timeStr = conv.third.second

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToChat(peerName) },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .background(Color(0xFFE8F5E9), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = peerName.firstOrNull()?.toString()?.uppercase() ?: "B",
                                                color = GreenPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = peerName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = timeStr,
                                                    color = TextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GreenSurface
                                                ) {
                                                    Text(
                                                        text = peerRole,
                                                        fontSize = 10.sp,
                                                        color = GreenDark,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                if (cropName.isNotBlank()) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "• $cropName",
                                                        fontSize = 11.sp,
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = lastMsg,
                                                color = TextSecondary,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Order Success Animation Overlay
            AnimatedVisibility(
                visible = showOrderSuccess,
                enter = fadeIn(tween(300)) + scaleIn(tween(300)),
                exit = fadeOut(tween(300)) + scaleOut(tween(300)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(48.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Order Secured!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Your order has been placed successfully.", fontSize = 14.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
            
            // Auto dismiss animation
            if (showOrderSuccess) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    showOrderSuccess = false
                }
            }
        }
    }
}

@Composable
fun DropdownFilter(label: String, options: List<String>, selected: String, onSelectionChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = if (selected == "All" || selected == "Any") label else selected

    Box {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (selected != "All" && selected != "Any") GreenSurface else Color(0xFFF5F5F5),
            border = if (selected != "All" && selected != "Any") androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary) else null,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayValue,
                    fontSize = 13.sp,
                    color = if (selected != "All" && selected != "Any") GreenPrimary else TextSecondary,
                    fontWeight = if (selected != "All" && selected != "Any") FontWeight.SemiBold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = if (selected != "All" && selected != "Any") GreenPrimary else TextSecondary, modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ListingCard(listing: CropListing, onPlaceOrder: () -> Unit, onNavigateToChat: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CropImageThumbnail(
                    imageUri = listing.imageUri,
                    emoji = listing.emoji,
                    emojiBg = listing.emojiBg,
                    size = 46.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = listing.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        if (!listing.grade.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = GreenSurface,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = listing.grade,
                                    color = GreenDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
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

            val displayQty = if (listing.originalQuantity != null && !listing.originalUnit.isNullOrBlank()) {
                val qStr = if (listing.originalQuantity % 1.0 == 0.0) listing.originalQuantity.toInt().toString() else listing.originalQuantity.toString()
                "$qStr ${listing.originalUnit}"
            } else {
                "${listing.quantityTons} Tons"
            }

            val displayPrice = if (listing.originalPrice != null && !listing.originalUnit.isNullOrBlank()) {
                "\u20B9${listing.originalPrice.toFormattedString()} / ${listing.originalUnit.removeSuffix("s")}"
            } else {
                "\u20B9${listing.pricePerTon.toFormattedString()} / Ton"
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Quantity", fontSize = 11.sp, color = TextSecondary)
                    Text(text = displayQty, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(text = "Asking Price", fontSize = 11.sp, color = TextSecondary)
                    Text(text = displayPrice, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OrangeText)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Market Avg", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "\u20B9${(listing.pricePerTon * 0.95).toInt().toFormattedString()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF1976D2))
                ) {
                    Text("Negotiate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(
                    onClick = onPlaceOrder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Place Order", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
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
