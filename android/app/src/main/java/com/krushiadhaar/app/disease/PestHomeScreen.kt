package com.krushiadhaar.app.disease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    onUploadClick: () -> Unit = {}
) {
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            com.krushiadhaar.app.GlobalMockData.selectedImageUri = uri.toString()
            com.krushiadhaar.app.GlobalMockData.capturedBitmap = null
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AppSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo", color = AppSurface, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
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
                Spacer(modifier = Modifier.height(16.dp))
                
                // History Item 1
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Text("🌱", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cotton Leaf Spot", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("2 days ago • Moderate", fontSize = 12.sp, color = TextSecondary)
                        }
                        TextButton(onClick = { /* View Report */ }) {
                            Text("View", color = GreenPrimary)
                        }
                    }
                }
                
                // History Item 2
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Text("🌾", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Wheat Rust", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("1 week ago • Severe", fontSize = 12.sp, color = TextSecondary)
                        }
                        TextButton(onClick = { /* View Report */ }) {
                            Text("View", color = GreenPrimary)
                        }
                    }
                }
            }
        }
    }
}
