package com.krushiadhaar.app.disease

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krushiadhaar.app.ui.theme.*

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentGuideScreen(
    onBack: () -> Unit = {},
    viewModel: DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val data = (uiState as? DiseaseUiState.Success)?.data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Treatment Guide", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val managementSteps = data?.management?.split(";")?.filter { it.isNotBlank() } ?: listOf("Immediate action required.")
            val treatmentSteps = data?.treatment?.split(";")?.filter { it.isNotBlank() } ?: listOf("No treatment specified.")

            Text("Management Practices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            managementSteps.forEachIndexed { index, step ->
                TreatmentStep((index + 1).toString(), step.trim())
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Treatment Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            treatmentSteps.forEachIndexed { index, step ->
                TreatmentStep((index + 1).toString(), step.trim())
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Recommended Products", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Medication, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Imidacloprid 17.8% SL", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Dosage: 0.5 ml / Litre of water", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Additional Tips", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GreenSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(data?.prevention ?: "For future seasons, ensure you plant certified seeds and follow proper crop rotation.", color = GreenText)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", color = AppSurface)
            }
        }
    }
}

@Composable
fun TreatmentStep(number: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Surface(
            shape = RoundedCornerShape(50),
            color = GreenPrimary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number, color = AppSurface, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
    }
}
