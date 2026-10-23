package com.krushiadhaar.app.disease

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.graphics.Bitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestHomeScreen(
    onTakePhotoClick: () -> Unit = {},
    onUploadClick: () -> Unit = {},
    onViewRecentScan: (com.krushiadhaar.app.GlobalMockData.PestScanHistory) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            com.krushiadhaar.app.GlobalMockData.selectedImageUri = uri.toString()
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                    android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                com.krushiadhaar.app.GlobalMockData.capturedBitmap = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onUploadClick()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            com.krushiadhaar.app.GlobalMockData.capturedBitmap = bitmap
            com.krushiadhaar.app.GlobalMockData.selectedImageUri = null
            onTakePhotoClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pest Detection", color = TextPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.krushiadhaar.app.R.drawable.pest_tab),
                    contentDescription = "Pest Detection Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Identify Diseases Instantly",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Take a photo of the affected plant part to get AI-powered diagnosis and treatment recommendations.",
                        color = AppSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = (-40).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val supportedCrops = listOf(
                        "Auto-Detect" to "🔍",
                        "Pumpkin" to "🎃",
                        "Tomato" to "🍅",
                        "Cotton" to "🌿",
                        "Chilli" to "🌶️",
                        "Rice" to "🌾",
                        "Onion" to "🧅",
                        "Sugarcane" to "🎋",
                        "Potato" to "🥔",
                        "Maize" to "🌽",
                        "Groundnut" to "🥜",
                        "Brinjal" to "🍆",
                        "Bitter Gourd" to "🥒",
                        "Ridge Gourd" to "🥬"
                    )
                    val selectedCrop = com.krushiadhaar.app.GlobalMockData.pestSelectedCrop.value

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Crop: $selectedCrop",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Choose crop below",
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenPrimary
                        )
                    }

                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        items(supportedCrops.size) { index ->
                            val (cropName, emoji) = supportedCrops[index]
                            val isSelected = selectedCrop == cropName
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) GreenPrimary else Color(0xFFF1F8E9),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) GreenPrimary else Color(0xFFC8E6C9)
                                ),
                                modifier = Modifier.clickable {
                                    com.krushiadhaar.app.GlobalMockData.pestSelectedCrop.value = cropName
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(emoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cropName,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AppSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo", color = AppSurface, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = GreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload from Gallery", color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Tips for best results:", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Ensure the affected part is in focus\n• Good lighting helps diagnosis\n• Include the whole leaf if possible", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Recent Scans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(14.dp))
                
                val recentScans = com.krushiadhaar.app.GlobalMockData.pestScanHistoryList
                if (recentScans.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No recent scans yet. Take a photo to diagnose crop pests.", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                } else {
                    recentScans.take(6).forEach { scan ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable { onViewRecentScan(scan) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(scan.emoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${scan.crop} • ${scan.diseaseName}", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text("${scan.dateText} • ${scan.severity}", fontSize = 12.sp, color = TextSecondary)
                                }
                                TextButton(onClick = { onViewRecentScan(scan) }) {
                                    Text("View", color = GreenPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
