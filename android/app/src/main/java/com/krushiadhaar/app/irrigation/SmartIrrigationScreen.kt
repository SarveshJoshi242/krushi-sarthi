package com.krushiadhaar.app.irrigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*
import com.krushiadhaar.app.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartIrrigationScreen(
    onNavigateBack: () -> Unit
) {
    var selectedCrop by remember { mutableStateOf(com.krushiadhaar.app.GlobalMockData.farmerCrop.value.ifBlank { "Wheat" }) }
    var sowingDays by remember { mutableIntStateOf(28) }

    val crops = listOf("Wheat", "Sugarcane", "Cotton", "Soybean")

    val totalDays = when (selectedCrop) {
        "Sugarcane" -> 360
        "Cotton" -> 160
        "Soybean" -> 100
        else -> 120
    }

    val progress = minOf(1.0f, sowingDays.toFloat() / totalDays.toFloat())

    val stages = when (selectedCrop) {
        "Sugarcane" -> listOf(
            Triple("Germination Phase", "0-45 Days", true),
            Triple("Tillering Phase", "45-120 Days", sowingDays >= 45),
            Triple("Grand Growth Phase", "120-270 Days", sowingDays >= 120),
            Triple("Ripening Phase", "270-360 Days", sowingDays >= 270)
        )
        "Cotton" -> listOf(
            Triple("Seedling Phase", "0-30 Days", true),
            Triple("Squaring Phase", "30-60 Days", sowingDays >= 30),
            Triple("Flowering & Boll Formation", "60-110 Days", sowingDays >= 60),
            Triple("Boll Opening & Maturity", "110-160 Days", sowingDays >= 110)
        )
        else -> listOf(
            Triple("Crown Root Initiation (CRI)", "20-25 Days", true),
            Triple("Tillering Stage", "40-45 Days", sowingDays >= 40),
            Triple("Late Jointing Stage", "60-65 Days", sowingDays >= 60),
            Triple("Flowering Stage", "80-85 Days", sowingDays >= 80),
            Triple("Milking & Dough Stage", "100-105 Days", sowingDays >= 100)
        )
    }

    val checklist = remember {
        mutableStateListOf(
            Pair("Land Preparation & Basal Fertilizer", true),
            Pair("Seed Sowing & First Watering", true),
            Pair("CRI Critical Irrigation", true),
            Pair("Weeding & 1st Urea Top Dressing", false),
            Pair("Foliar Micronutrient Spray (Zinc/Boron)", false),
            Pair("Flowering Stage Scheduled Irrigation", false),
            Pair("Pre-Harvest Field Drainage", false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(AppStrings.smartIrrigation, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("Weather-Adaptive Water & Activity Scheduler", fontSize = 11.sp, color = GreenPrimary)
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
            // Crop Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                crops.forEach { crop ->
                    val isSelected = selectedCrop == crop
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) GreenPrimary else Color.White,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCrop = crop }
                    ) {
                        Text(
                            text = crop,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Growth Progress Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Crop Cycle", fontSize = 12.sp, color = TextSecondary)
                            Text("$selectedCrop (Day $sowingDays of $totalDays)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenSurface
                        ) {
                            Text(
                                "${(progress * 100).toInt()}% Growth",
                                color = GreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GreenPrimary,
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }

            // Next Irrigation Alert Card (Integrated with weather forecast)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF90CAF9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Next Critical Irrigation", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0D47A1))
                            Text("Scheduled in 2 Days (Day 30)", fontSize = 12.sp, color = Color(0xFF1976D2))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFBBDEFB))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Water Volume: ~220,000 Litres / acre", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))
                    Spacer(modifier = Modifier.height(6.dp))

                    // Weather Smart Delay Tip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("🌧️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Weather Alert: Rain forecasted this weekend. Smart sensor suggests holding irrigation by 48 hours to save pump electricity and prevent waterlogging.",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Growth Stages Milestones
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Critical Irrigation Stages", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    stages.forEachIndexed { index, (stageName, window, isPast) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isPast) GreenPrimary else Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPast) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Text("${index + 1}", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stageName, fontSize = 13.sp, fontWeight = if (isPast) FontWeight.SemiBold else FontWeight.Normal, color = TextPrimary)
                                Text(window, fontSize = 11.sp, color = TextSecondary)
                            }
                            if (isPast) {
                                Text("Passed", fontSize = 11.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
                            } else {
                                Text("Upcoming", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Interactive Farm Activity Checklist
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Crop Care Checklist", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    checklist.forEachIndexed { idx, (task, isDone) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { checklist[idx] = Pair(task, !isDone) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { checklist[idx] = Pair(task, it) },
                                colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = task,
                                fontSize = 13.sp,
                                color = if (isDone) TextSecondary else TextPrimary,
                                fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
