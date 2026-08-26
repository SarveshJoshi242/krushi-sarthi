package com.krushiadhaar.app.management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CropManagementScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Crop & Yield Management", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Harvest Yield", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ManagementCard("Crop Recommendation", "Based on season/time")
        Spacer(modifier = Modifier.height(8.dp))
        ManagementCard("Weather Updates", "Forecast and alerts")
        Spacer(modifier = Modifier.height(8.dp))
        ManagementCard("Soil Nutrients", "Techniques and learning")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ManagementCard("Stock Inventory", "Seeds, Fertilizer, Harvested Crop")
        Spacer(modifier = Modifier.height(8.dp))
        ManagementCard("Financial Estimation", "Cost calculation per area")
        Spacer(modifier = Modifier.height(8.dp))
        ManagementCard("Machinery & Tools", "Availability and rental")

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ManagementCard(title: String, subtitle: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
