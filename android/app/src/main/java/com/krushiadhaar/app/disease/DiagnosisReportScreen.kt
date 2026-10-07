package com.krushiadhaar.app.disease

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krushiadhaar.app.ui.theme.*
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisReportScreen(
    onViewTreatment: () -> Unit = {},
    onDownload: () -> Unit = {},
    viewModel: DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val data = (uiState as? DiseaseUiState.Success)?.data
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnosis Report", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = AppBackground,
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onViewTreatment,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("View Treatment Guide", color = AppSurface, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDownload,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Report", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.krushiadhaar.app.R.drawable.mock_disease_photo),
                contentDescription = "Crop Report Generation",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = OrangeLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(data?.disease_name ?: "Unknown Disease", style = MaterialTheme.typography.titleLarge, color = OrangePrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(data?.scientific_name ?: "Unknown", color = OrangePrimary)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("What we found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column {
                FoundItem("Crop: ${data?.crop ?: ""}")
                FoundItem("Symptoms: ${data?.symptoms ?: ""}")
                FoundItem("Severity: ${data?.severity ?: ""}")
                FoundItem("Confidence: ${data?.confidence ?: 0.0}%")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("What to do now", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(data?.management ?: "Immediate action is required.", color = TextSecondary)
        }
    }
}

@Composable
fun FoundItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = TextPrimary)
    }
}
