package com.krushiadhaar.app.management

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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

data class ResourceBreakdown(
    val label: String,
    val percent: Float,
    val amount: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCalculatorScreen(onNavigateBack: () -> Unit) {
    val cropTypes = listOf("Wheat", "Rice", "Cotton", "Sugarcane", "Maize")
    var selectedCrop by remember { mutableStateOf("Wheat") }
    var expanded by remember { mutableStateOf(false) }
    var farmArea by remember { mutableStateOf("5") }

    val seedsChecked = remember { mutableStateOf(true) }
    val fertilizerChecked = remember { mutableStateOf(true) }
    val pesticidesChecked = remember { mutableStateOf(true) }
    val laborChecked = remember { mutableStateOf(true) }

    var showResult by remember { mutableStateOf(true) }

    val breakdowns = listOf(
        ResourceBreakdown("Fertilizer (40%)", 0.40f, 18000, GreenPrimary),
        ResourceBreakdown("Labor (35%)", 0.35f, 15750, Color(0xFF2196F3)),
        ResourceBreakdown("Seeds (25%)", 0.25f, 11250, OrangePrimary)
    )
    val totalBudget = breakdowns.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Calculator", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary) },
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
            // Crop Type Dropdown
            Column {
                Text(text = "Select Crop Type", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedCrop,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppDivider, focusedBorderColor = GreenPrimary)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        cropTypes.forEach { crop ->
                            DropdownMenuItem(
                                text = { Text(crop) },
                                onClick = { selectedCrop = crop; expanded = false }
                            )
                        }
                    }
                }
            }

            // Farm Area Input
            Column {
                Text(text = "Farm Area (in Acres)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = farmArea,
                    onValueChange = { farmArea = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = AppDivider, focusedBorderColor = GreenPrimary)
                )
            }

            // Required Resources
            Column {
                Text(text = "Required Resources", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ResourceCheckbox(label = "Seeds", checked = seedsChecked.value, onChecked = { seedsChecked.value = it }, modifier = Modifier.weight(1f))
                    ResourceCheckbox(label = "Fertilizer", checked = fertilizerChecked.value, onChecked = { fertilizerChecked.value = it }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ResourceCheckbox(label = "Pesticides", checked = pesticidesChecked.value, onChecked = { pesticidesChecked.value = it }, modifier = Modifier.weight(1f))
                    ResourceCheckbox(label = "Labor", checked = laborChecked.value, onChecked = { laborChecked.value = it }, modifier = Modifier.weight(1f))
                }
            }

            // Calculate Button
            Button(
                onClick = { showResult = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(text = "Calculate Estimated Cost", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            // Result Card
            if (showResult) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Estimated Budget", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GreenPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${totalBudget.toFormattedRupee()}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        breakdowns.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(item.color))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.label, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                                Text(text = "₹${item.amount.toFormattedRupee()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stacked bar chart
                        Row(
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp))
                        ) {
                            breakdowns.forEach { item ->
                                Box(modifier = Modifier.weight(item.percent).fillMaxHeight().background(item.color))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceCheckbox(label: String, checked: Boolean, onChecked: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            colors = CheckboxDefaults.colors(checkedColor = GreenPrimary, uncheckedColor = AppDivider)
        )
        Text(text = label, fontSize = 13.sp, color = TextPrimary)
    }
}

private fun Int.toFormattedRupee(): String {
    return if (this >= 1000) {
        val s = this.toString()
        val len = s.length
        buildString {
            s.forEachIndexed { i, c ->
                if (i > 0 && (len - i) % 2 == 0 && len > 3) append(",")
                append(c)
            }
        }
    } else this.toString()
}
