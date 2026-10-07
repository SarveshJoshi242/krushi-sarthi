package com.krushiadhaar.app.finance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryGreen = Color(0xFF8AC149)
private val BackgroundColor = Color(0xFFF8F9FA)
private val DarkText = Color(0xFF212121)
private val GrayText = Color(0xFF757575)
private val RedText = Color(0xFFE53935)
private val OrangeText = Color(0xFFFF9800)

data class MarketData(
    val cropName: String,
    val marketStatus: MarketStatus,
    val marketPrice: Int,
    val govtMsp: Int
) {
    val difference: Int get() = marketPrice - govtMsp
}

enum class MarketStatus(val label: String, val color: Color, val bgColor: Color) {
    HIGH("High Market", PrimaryGreen, PrimaryGreen.copy(alpha = 0.1f)),
    MODERATE("Moderate Market", OrangeText, OrangeText.copy(alpha = 0.1f)),
    LOW("Low Market", RedText, RedText.copy(alpha = 0.1f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerWalletScreen(
    onBackClick: () -> Unit = {}
) {
    val marketDataList = listOf(
        MarketData("Groundnut", MarketStatus.HIGH, 7497, 6783),
        MarketData("Soybean", MarketStatus.LOW, 4200, 4600),
        MarketData("Sunflower", MarketStatus.MODERATE, 6100, 6000)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Wallet", color = DarkText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // New Wallet Section
            val balance = com.krushiadhaar.app.GlobalMockData.walletBalance.value
            val transactions = com.krushiadhaar.app.GlobalMockData.transactions
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("₹$balance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 32.sp)
                }
            }
            
            if (transactions.isNotEmpty()) {
                Text(
                    text = "Recent Transactions",
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(0.5f), contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(transactions) { tx ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(if(tx.isCredit) PrimaryGreen.copy(alpha=0.1f) else RedText.copy(alpha=0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Text(if(tx.isCredit) "+" else "-", color = if(tx.isCredit) PrimaryGreen else RedText, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(tx.description, modifier = Modifier.weight(1f), fontSize = 14.sp, color = DarkText)
                            Text(if(tx.isCredit) "+₹${tx.amount}" else "-₹${tx.amount}", color = if(tx.isCredit) PrimaryGreen else RedText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(
                text = "Oilseed Market Data & Regional Intelligence",
                color = DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp)
            ) {
                items(marketDataList) { data ->
                    MarketDataCard(data = data)
                }
            }
        }
    }
}

@Composable
fun MarketDataCard(data: MarketData) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = data.cropName,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                
                Surface(
                    color = data.marketStatus.bgColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = data.marketStatus.label,
                        color = data.marketStatus.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Market Price", color = GrayText, fontSize = 12.sp)
                    Text(
                        text = "₹${data.marketPrice}/quintal",
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                
                Column {
                    Text("Govt MSP", color = GrayText, fontSize = 12.sp)
                    Text(
                        text = "₹${data.govtMsp}/quintal",
                        color = DarkText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("Difference", color = GrayText, fontSize = 12.sp)
                    val diffColor = if (data.difference >= 0) PrimaryGreen else RedText
                    val sign = if (data.difference >= 0) "+" else ""
                    Text(
                        text = "$sign₹${data.difference}/quintal",
                        color = diffColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
