package com.krushiadhaar.app.disease

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseDetectionScreen(onNavigateBack: () -> Unit) {
    var hasResult by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Plant Health Scanner", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upload Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.5.dp, AppDivider, RoundedCornerShape(14.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Upload or Capture Image", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Focus clearly on the affected leaf", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { hasResult = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Choose File", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (hasResult) {
                // Detection Result Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = RedError, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Cotton Leaf Blight", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RedError)
                            }
                            Text(text = "94% Match", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = 0.94f,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                            color = RedError,
                            trackColor = AppDivider
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "High confidence match based on visual symptoms.", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Text(text = "Treatment Plan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

                // Organic Treatment Card — green left border
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(IntrinsicSize.Min)
                                .background(GreenPrimary)
                        )
                        Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Organic Treatment", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GreenPrimary)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            TreatmentLine(label = "Remedy:", value = "Neem oil spray mixture.")
                            Spacer(modifier = Modifier.height(6.dp))
                            TreatmentLine(label = "Dosage:", value = "Mix 5ml Neem oil per 1L of water with a few drops of mild soap.")
                            Spacer(modifier = Modifier.height(6.dp))
                            TreatmentLine(label = "Instructions:", value = "Spray thoroughly on both sides of leaves early in the morning. Repeat every 7 days until cleared.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TreatmentLine(label: String, value: String) {
    Row {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
    }
}
