package com.krushiadhaar.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.AppBackground
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.GreenSurface
import com.krushiadhaar.app.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    onAnalyze: () -> Unit = {}
) {
    var state by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var farmArea by remember { mutableStateOf("") }
    var areaUnit by remember { mutableStateOf("Acres") }
    var selectedCrop by remember { mutableStateOf("Not Sown Yet") }

    val crops = listOf("Not Sown Yet", "Soyabean", "Groundnut")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Step 2 of 2",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add Your Farm",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text("District") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Crop Type", fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Horizontal scroll for chips could be added, but row works for 3 short items
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            crops.forEach { crop ->
                val isSelected = selectedCrop == crop
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCrop = crop },
                    label = { Text(crop) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenSurface,
                        selectedLabelColor = GreenPrimary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Farm Area", fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = farmArea,
                onValueChange = { farmArea = it },
                label = { Text("Area") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = GreenPrimary
                )
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    RadioButton(
                        selected = areaUnit == "Acres",
                        onClick = { areaUnit = "Acres" },
                        colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                    )
                    Text("Acres", modifier = Modifier.padding(top = 12.dp))
                }
                Row {
                    RadioButton(
                        selected = areaUnit == "Hectares",
                        onClick = { areaUnit = "Hectares" },
                        colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                    )
                    Text("Hectares", modifier = Modifier.padding(top = 12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAnalyze,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Analyze My Farm", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
