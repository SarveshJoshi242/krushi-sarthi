package com.krushiadhaar.app.buyer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboardScreen(
    onNavigateToMarketplace: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buyer Dashboard", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimary)
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Welcome back, Buyer!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Marketplace Module
            Card(
                onClick = onNavigateToMarketplace,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OrangePrimary)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Marketplace", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Browse & Buy Crops", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Market Trends Data Vis
            Text("Market Trends", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Average Prices (/Ton)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple Bar Chart
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = 40.dp.toPx()
                            val spacing = (size.width - (barWidth * 3)) / 4
                            val maxPrice = 9000f
                            
                            val prices = listOf(2600f, 2400f, 8100f)
                            val colors = listOf(Color(0xFF81C784), Color(0xFFE57373), Color(0xFF64B5F6))
                            
                            prices.forEachIndexed { index, price ->
                                val barHeight = (price / maxPrice) * size.height
                                val startX = spacing + (index * (barWidth + spacing))
                                val startY = size.height - barHeight
                                
                                drawRoundRect(
                                    color = colors[index],
                                    topLeft = Offset(startX, startY),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text("Wheat", fontSize = 12.sp, color = TextSecondary)
                        Text("Sugarcane", fontSize = 12.sp, color = TextSecondary)
                        Text("Cotton", fontSize = 12.sp, color = TextSecondary)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = AppDivider)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Stats
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Wheat Demand", fontSize = 12.sp, color = TextMuted)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("High", fontWeight = FontWeight.Bold, color = GreenPrimary)
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                        Column {
                            Text("Sugarcane", fontSize = 12.sp, color = TextMuted)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Low", fontWeight = FontWeight.Bold, color = RedError)
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = RedError, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Direct Messages Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Direct Messages", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                TextButton(onClick = onNavigateToNotifications) {
                    Text("View All", color = GreenPrimary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth().clickable { onNavigateToChat("Suresh Kumar") }.padding(vertical = 8.dp)) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("S", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Suresh Kumar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("2h ago", fontSize = 12.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Yes, I can deliver it by tomorrow morning if we confirm the order now.", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = AppDivider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth().clickable { onNavigateToChat("Ramesh Patil") }.padding(vertical = 8.dp)) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("R", color = GreenPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ramesh Patil", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Yesterday", fontSize = 12.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Let me know when you are ready to negotiate for the Sugarcane batch.", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
