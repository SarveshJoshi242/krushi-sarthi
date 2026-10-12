package com.krushiadhaar.app.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.*
import com.krushiadhaar.app.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(AppStrings.pdfReport, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("Official Farm Health & Bank Loan Statement", fontSize = 11.sp, color = GreenPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        isGenerating = true
                        PdfReportGenerator.generateAndShareReport(context)
                        isGenerating = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isGenerating) "Generating Document..." else "Generate & Export Official PDF",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = AppBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview Certificate Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GreenSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📄", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Farm Health & Revenue Certificate", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text("Standard A4 Verified Document", fontSize = 12.sp, color = GreenPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "This report compiles your farm's geo-coordinates, live crop cycle status, wallet & sales revenue, soil NPK analysis, and AI plant pathology scans into a verified PDF ready for bank loan applications (KCC) or crop insurance claims (PMFBY).",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            val farmerName = GlobalMockData.farmerName.value.ifBlank { "Sarvesh Patil" }
            val location = GlobalMockData.userLocation.value
            val area = "${GlobalMockData.farmerFarmArea.value} ${GlobalMockData.farmerAreaUnit.value}"
            val coords = "${String.format(java.util.Locale.US, "%.4f", GlobalMockData.farmerLatitude.value)}° N, ${String.format(java.util.Locale.US, "%.4f", GlobalMockData.farmerLongitude.value)}° E"
            val crop = GlobalMockData.farmerCrop.value.ifBlank { "Wheat (Sharbati)" }

            // Summary Items Preview
            ReportSectionCard(
                icon = Icons.Default.Person,
                title = "1. Farmer & Land Profile",
                details = listOf(
                    "Farmer Name: $farmerName",
                    "Location: $location",
                    "Total Land: $area",
                    "GPS Coordinates: $coords"
                )
            )

            ReportSectionCard(
                icon = Icons.Default.Eco,
                title = "2. Production & Crop Yield",
                details = listOf(
                    "Active Crop: $crop",
                    "Cycle Progress: Active Vegetative Season",
                    "Expected Yield: 18.5 Tons",
                    "Irrigation Status: Drip System Active"
                )
            )

            val wallet = GlobalMockData.walletBalance.value
            val rev = GlobalMockData.cropYieldRevenue.value
            ReportSectionCard(
                icon = Icons.Default.AccountBalanceWallet,
                title = "3. Financial Ledger Summary",
                details = listOf(
                    "Wallet Balance: ₹$wallet",
                    "Recorded Season Revenue: ₹$rev",
                    "Estimated Input Cost: ₹16,800",
                    "Net Farm Profit: ₹${if (rev > 0) rev - 16800 else wallet - 16800}"
                )
            )

            ReportSectionCard(
                icon = Icons.Default.Science,
                title = "4. Soil Health Diagnostic (NPK)",
                details = listOf(
                    "Nitrogen (N): 210 kg/ha (Low Deficit) → Prescribed 2.1 Bags Urea",
                    "Phosphorus (P): 18 kg/ha (Optimal) → Prescribed 1.1 Bags DAP",
                    "Potassium (K): 220 kg/ha (Optimal) → Prescribed 0.7 Bags MOP",
                    "Soil pH: 7.2 (Neutral & Balanced)"
                )
            )

            ReportSectionCard(
                icon = Icons.Default.Security,
                title = "5. AI Pathology & Disease History",
                details = listOf(
                    "Latest Scan Status: Verified Passed",
                    "AI Diagnosis: Leaf Blight (Mild)",
                    "Confidence Score: 98% (TensorFlow CNN)",
                    "Prescription: Mancozeb (2g/L) + Neem Oil Spray"
                )
            )
        }
    }
}

@Composable
private fun ReportSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    details: List<String>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            details.forEach { line ->
                Text("• $line", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}
