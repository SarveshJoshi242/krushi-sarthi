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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krushiadhaar.app.ui.theme.*
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.asImageBitmap

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
            val bitmapState = com.krushiadhaar.app.GlobalMockData.capturedBitmap
            if (bitmapState != null) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.graphics.painter.BitmapPainter(bitmapState.asImageBitmap()),
                    contentDescription = "Crop Report Generation",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else if (com.krushiadhaar.app.GlobalMockData.selectedImageUri != null) {
                val context = androidx.compose.ui.platform.LocalContext.current
                var uriBitmap by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<android.graphics.Bitmap?>(null) }
                
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    val uri = android.net.Uri.parse(com.krushiadhaar.app.GlobalMockData.selectedImageUri)
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                            android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                decoder.isMutableRequired = true
                            }.let { uriBitmap = it }
                        } else {
                            @Suppress("DEPRECATION")
                            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)?.let { uriBitmap = it }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                if (uriBitmap != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.graphics.painter.BitmapPainter(uriBitmap!!.asImageBitmap()),
                        contentDescription = "Crop Report Generation",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.krushiadhaar.app.R.drawable.mock_disease_photo),
                        contentDescription = "Crop Report Generation",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            } else {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.krushiadhaar.app.R.drawable.mock_disease_photo),
                    contentDescription = "Crop Report Generation",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
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
            
            Text("Recommended Treatment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column {
                FoundItem("Medicines: ${data?.medicines ?: ""}")
                FoundItem("Estimated Cost: ₹${data?.estimatedCost ?: 0.0}")
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
