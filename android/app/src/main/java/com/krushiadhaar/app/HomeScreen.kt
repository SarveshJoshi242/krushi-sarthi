package com.krushiadhaar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDiseaseDetection: () -> Unit,
    onNavigateToManagement: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToExpense: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Welcome back,", fontSize = 14.sp, color = TextSecondary)
                Text(text = "Farmer", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextSecondary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Weather Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(GreenPrimary, GreenDark)))
                .padding(18.dp)
        ) {
            Column {
                Text(text = "CURRENT WEATHER", fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f), letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "28\u00b0C", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Cloud, contentDescription = "Cloud", tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    WeatherChip(label = "75%", sublabel = "Humidity")
                    Spacer(modifier = Modifier.width(12.dp))
                    WeatherChip(label = "Light Rain", sublabel = "expected")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83C\uDF3E", title = "Pitch Crop", subtitle = "Market", onClick = {})
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83D\uDCF7", title = "Disease Scan", subtitle = "AI Tool", onClick = onNavigateToDiseaseDetection)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83D\uDCE6", title = "Inventory", subtitle = "Resources", onClick = onNavigateToManagement)
            QuickActionCard(modifier = Modifier.weight(1f), emoji = "\uD83E\uDDEE", title = "Expenses", subtitle = "Calculator", onClick = onNavigateToExpense)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Active Listings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            TextButton(onClick = {}) { Text(text = "View All", fontSize = 13.sp, color = GreenPrimary, fontWeight = FontWeight.SemiBold) }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun WeatherChip(label: String, sublabel: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = sublabel, fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(modifier: Modifier = Modifier, emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(GreenSurface), contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
