package com.krushiadhaar.app.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

data class PurchaseHistory(
    val cropName: String,
    val farmerName: String,
    val date: String,
    val amountPaid: String,
    val quantity: String,
    val status: String
)

private val sampleHistory = listOf(
    PurchaseHistory("Sugarcane", "Ramesh Patil", "Oct 2, 2026", "₹25,000", "10 Tons", "Delivered"),
    PurchaseHistory("Wheat", "Kisan Rao", "Sep 15, 2026", "₹12,000", "5 Tons", "Delivered")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHistoryScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase History", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleHistory) { purchase ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = purchase.cropName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = purchase.status, color = GreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Farmer: ${purchase.farmerName}", fontSize = 14.sp, color = TextSecondary)
                        Text(text = "Date: ${purchase.date}", fontSize = 14.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = AppDivider)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Quantity", fontSize = 12.sp, color = TextMuted)
                                Text(purchase.quantity, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Paid", fontSize = 12.sp, color = TextMuted)
                                Text(purchase.amountPaid, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
