package com.krushiadhaar.app.disease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.krushiadhaar.app.ui.theme.*

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PestAnalysisScreen(
    onAnalysisComplete: () -> Unit = {},
    viewModel: DiseaseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel.progress.collectAsState()

    LaunchedEffect(Unit) {
        // Mock image bytes for now since we don't have a real camera hooked up
        viewModel.analyzeImage(ByteArray(100))
    }

    LaunchedEffect(uiState) {
        if (uiState is DiseaseUiState.Success) {
            onAnalysisComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AI is analyzing your photo", style = MaterialTheme.typography.headlineSmall, color = AppSurface)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(120.dp),
                color = GreenPrimary,
                strokeWidth = 8.dp,
                trackColor = Color.DarkGray
            )
            Text("${(progress * 100).toInt()}%", color = AppSurface, style = MaterialTheme.typography.titleLarge)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            ChecklistItem("Image quality check", isDone = true)
            ChecklistItem("Detecting patterns", isDone = true)
            ChecklistItem("Identifying issues...", isDone = false)
        }
        
        // Dummy button for navigation simulation
        Spacer(modifier = Modifier.height(64.dp))
        Button(onClick = onAnalysisComplete, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
            Text("Simulate Complete")
        }
    }
}

@Composable
fun ChecklistItem(text: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        if (isDone) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary)
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = GreenPrimary, strokeWidth = 2.dp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = if (isDone) AppSurface else Color.LightGray)
    }
}
