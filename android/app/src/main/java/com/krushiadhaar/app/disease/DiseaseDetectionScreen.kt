package com.krushiadhaar.app.disease

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DiseaseDetectionScreen(onNavigateBack: () -> Unit) {
    var imageUploaded by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Disease Detection (ML)", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        if (!imageUploaded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No Image Selected", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = { imageUploaded = true; analysisResult = "Analyzing..." }) {
                                Text("Take Photo")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = { imageUploaded = true; analysisResult = "Analyzing..." }) {
                                Text("Upload Photo")
                            }
                        }
                    }
                }
            }
        } else {
            // Simulated Results
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Analysis Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Detected Disease: Leaf Blight (94% confidence)", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Recommended Action:", fontWeight = FontWeight.Bold)
                    Text("1. Remove infected leaves immediately.")
                    Text("2. Apply Copper-based fungicide.")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Estimated Cost to Resolve: ₹500 - ₹1200", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { imageUploaded = false; analysisResult = "" }, modifier = Modifier.fillMaxWidth()) {
                Text("Scan Another Crop")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
