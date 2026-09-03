package com.krushiadhaar.app.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class InventoryItem(
    val name: String,
    val status: String,
    val statusOk: Boolean,
    val emoji: String
)

private val consumables = listOf(
    InventoryItem("Urea Fertilizer", "Low Stock (10kg left)", statusOk = false, emoji = "\u2697\uFE0F"),
    InventoryItem("Wheat Seeds", "In Stock (24kg)", statusOk = true, emoji = "\uD83C\uDF3E")
)

private val machinery = listOf(
    InventoryItem("Tractor", "Operational", statusOk = true, emoji = "\uD83D\uDE9C"),
    InventoryItem("Sprinkler Pump", "Needs Repair", statusOk = false, emoji = "\uD83D\uDCA7")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropManagementScreen(onNavigateBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Consumables", "Machinery")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Inventory", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val currentItems = if (selectedTab == 0) consumables else machinery

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(currentItems) { item ->
                    InventoryItemCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun InventoryItemCard(item: InventoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (item.statusOk) GreenSurface else RedLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = item.status, fontSize = 12.sp, color = if (item.statusOk) GreenPrimary else RedError)
            }
            Icon(
                imageVector = if (item.statusOk) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (item.statusOk) GreenPrimary else RedError,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
