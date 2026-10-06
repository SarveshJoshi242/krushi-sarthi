package com.krushiadhaar.app.documents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(onNavigateBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documents & Eligibility") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Verified Documents", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("✅ Aadhar Card (XXXX-XXXX-1234)", style = MaterialTheme.typography.bodyLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("✅ 7/12 Land Extract (Kolhapur)", style = MaterialTheme.typography.bodyLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("❌ Soil Health Card (Pending)", style = MaterialTheme.typography.bodyLarge, color = androidx.compose.ui.graphics.Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Upload Missing Documents")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Scheme Eligibility", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFE8F5E9))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🎉 PM-Kisan Samman Nidhi", style = MaterialTheme.typography.titleSmall, color = androidx.compose.ui.graphics.Color(0xFF2E7D32))
                    Text("Eligible based on 7/12 extract and Aadhar.", style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color(0xFF2E7D32))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFEBEE))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ MahaDBT Tractor Subsidy", style = MaterialTheme.typography.titleSmall, color = androidx.compose.ui.graphics.Color(0xFFC62828))
                    Text("Not eligible. Requires minimum 10 acres.", style = MaterialTheme.typography.bodySmall, color = androidx.compose.ui.graphics.Color(0xFFC62828))
                }
            }
        }
    }
}
