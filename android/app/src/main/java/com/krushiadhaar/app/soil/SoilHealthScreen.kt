package com.krushiadhaar.app.soil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*
import com.krushiadhaar.app.util.AppStrings
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilHealthScreen(
    onNavigateBack: () -> Unit
) {
    var selectedCrop by remember { mutableStateOf(com.krushiadhaar.app.GlobalMockData.farmerCrop.value.ifBlank { "Wheat" }) }
    var acreage by remember { mutableStateOf(com.krushiadhaar.app.GlobalMockData.farmerFarmArea.value.ifBlank { "5.0" }) }

    var nitrogen by remember { mutableFloatStateOf(210f) }      // kg/ha
    var phosphorus by remember { mutableFloatStateOf(18f) }     // kg/ha
    var potassium by remember { mutableFloatStateOf(220f) }     // kg/ha
    var soilPh by remember { mutableFloatStateOf(7.2f) }

    // Evaluation logic
    val nStatus = if (nitrogen < 280) "Low (Deficient)" else if (nitrogen <= 560) "Medium (Optimal)" else "High (Excess)"
    val pStatus = if (phosphorus < 10) "Low (Deficient)" else if (phosphorus <= 25) "Medium (Optimal)" else "High (Excess)"
    val kStatus = if (potassium < 110) "Low (Deficient)" else if (potassium <= 280) "Medium (Optimal)" else "High (Excess)"

    val ac = acreage.toDoubleOrNull() ?: 1.0

    // Fertilizer calculations (50 kg bags for total acreage)
    val factorN = if (nitrogen < 280) 1.3 else if (nitrogen <= 560) 1.0 else 0.7
    val factorP = if (phosphorus < 10) 1.3 else if (phosphorus <= 25) 1.0 else 0.7
    val factorK = if (potassium < 110) 1.3 else if (potassium <= 280) 1.0 else 0.7

    val reqN = 50.0 * factorN * ac
    val reqP = 25.0 * factorP * ac
    val reqK = 20.0 * factorK * ac

    val dapBags = Math.round((reqP / 23.0) * 10.0) / 10.0
    val nFromDap = dapBags * 9.0
    val remainingN = maxOf(0.0, reqN - nFromDap)
    val ureaBags = Math.round((remainingN / 23.0) * 10.0) / 10.0
    val mopBags = Math.round((reqK / 30.0) * 10.0) / 10.0

    // pH Advice
    val (phStatus, phAdvice, phColor) = when {
        soilPh < 6.0f -> Triple(
            "Acidic (pH < 6.0)",
            "Apply Agricultural Lime (Calcium Carbonate) @ 250 kg/acre to neutralize acidity and improve nutrient uptake.",
            Color(0xFFE53935)
        )
        soilPh > 8.0f -> Triple(
            "Alkaline / Saline (pH > 8.0)",
            "Apply Gypsum @ 200 kg/acre with organic green manure or compost to reduce sodium and soil salinity.",
            Color(0xFFFF9800)
        )
        else -> Triple(
            "Optimal / Neutral (6.0 - 7.5)",
            "Soil pH is balanced. Excellent biological microbial activity and ideal for major crop growth.",
            GreenPrimary
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(AppStrings.soilHealth, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("NPK Nutrient Diagnostics & Bag Prescriptions", fontSize = 11.sp, color = GreenPrimary)
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
            // Configuration Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1. Field & Crop Parameters", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Target Crop", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = selectedCrop,
                                onValueChange = { selectedCrop = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Acreage (Acres)", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = acreage,
                                onValueChange = { acreage = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Sliders Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("2. Soil Health Card Test Values", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Nitrogen
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Available Nitrogen (N)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${nitrogen.roundToInt()} kg/ha ($nStatus)", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = nitrogen,
                        onValueChange = { nitrogen = it },
                        valueRange = 100f..700f,
                        colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Phosphorus
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Available Phosphorus (P)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${phosphorus.roundToInt()} kg/ha ($pStatus)", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = phosphorus,
                        onValueChange = { phosphorus = it },
                        valueRange = 5f..50f,
                        colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Potassium
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Available Potassium (K)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${potassium.roundToInt()} kg/ha ($kStatus)", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = potassium,
                        onValueChange = { potassium = it },
                        valueRange = 50f..400f,
                        colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // pH
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Soil pH Level", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(String.format("%.1f (%s)", soilPh, phStatus), fontSize = 12.sp, color = phColor, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = soilPh,
                        onValueChange = { soilPh = it },
                        valueRange = 4.5f..9.5f,
                        colors = SliderDefaults.colors(thumbColor = phColor, activeTrackColor = phColor)
                    )
                }
            }

            // Prescription Output Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = GreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Prescribed Fertilizer Bags (for $ac Acres)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GreenDark)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FertilizerBagBox(title = "Urea (46% N)", bags = ureaBags, emoji = "🌾", modifier = Modifier.weight(1f))
                        FertilizerBagBox(title = "DAP (18:46:0)", bags = dapBags, emoji = "🧪", modifier = Modifier.weight(1f))
                        FertilizerBagBox(title = "MOP (60% K)", bags = mopBags, emoji = "🌿", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GreenPrimary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // pH Recommendation
                    Text("Soil Condition Amendment:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(phAdvice, fontSize = 12.sp, color = TextPrimary, lineHeight = 18.sp)
                }
            }

            // Schedule Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Application Schedule", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    ScheduleStep(
                        step = "1. Basal (At Sowing)",
                        desc = "Apply 100% DAP ($dapBags bags) + 100% MOP ($mopBags bags) + 33% Urea (${Math.round(ureaBags * 0.33 * 10.0)/10.0} bags) during final land preparation."
                    )
                    ScheduleStep(
                        step = "2. 1st Top Dressing (Day 30)",
                        desc = "Apply 33% Urea (${Math.round(ureaBags * 0.33 * 10.0)/10.0} bags) along with first weeding and irrigation."
                    )
                    ScheduleStep(
                        step = "3. 2nd Top Dressing (Day 55)",
                        desc = "Broadcast remaining 34% Urea (${Math.round(ureaBags * 0.34 * 10.0)/10.0} bags) right before the flowering initiation stage."
                    )
                }
            }
        }
    }
}

@Composable
private fun FertilizerBagBox(title: String, bags: Double, emoji: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$bags Bags", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            Text(title, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun ScheduleStep(step: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(step, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = GreenDark)
        Spacer(modifier = Modifier.height(2.dp))
        Text(desc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
    }
}
