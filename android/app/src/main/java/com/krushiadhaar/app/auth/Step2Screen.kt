package com.krushiadhaar.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.AppBackground
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.GreenSurface
import com.krushiadhaar.app.ui.theme.TextPrimary
import com.krushiadhaar.app.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    onAnalyze: () -> Unit = {}
) {
    var state by remember { mutableStateOf(GlobalMockData.farmerState.value) }
    var district by remember { mutableStateOf(GlobalMockData.farmerDistrict.value) }
    var farmArea by remember { mutableStateOf(GlobalMockData.farmerFarmArea.value) }
    var areaUnit by remember { mutableStateOf(GlobalMockData.farmerAreaUnit.value) }
    var selectedCrop by remember { mutableStateOf(GlobalMockData.farmerCrop.value) }

    val crops = listOf(
        "Soyabean", "Wheat", "Sugarcane", "Cotton", "Rice", 
        "Onion", "Potato", "Maize", "Groundnut", "Not Sown Yet"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = AppStrings.step2Subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = AppStrings.addYourFarmTitle,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text(AppStrings.stateLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary
            )
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text(AppStrings.districtLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = AppStrings.cropTypeLabel,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Crop Selection Chips in wrapped flow
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            crops.forEach { crop ->
                val isSelected = selectedCrop == crop
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCrop = crop },
                    label = { Text(crop, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenSurface,
                        selectedLabelColor = GreenPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = AppStrings.farmAreaLabel,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = farmArea,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' }) {
                        farmArea = input
                    }
                },
                label = { Text(AppStrings.farmAreaLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1.2f),
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
                    Text(AppStrings.acresUnit, modifier = Modifier.padding(top = 12.dp))
                }
                Row {
                    RadioButton(
                        selected = areaUnit == "Hectares",
                        onClick = { areaUnit = "Hectares" },
                        colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                    )
                    Text(AppStrings.hectaresUnit, modifier = Modifier.padding(top = 12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                val finalArea = if (farmArea.isNotBlank()) farmArea.trim() else "5"
                val finalUnit = areaUnit

                // Persist Complete Farmer and Farm Identity
                GlobalMockData.saveFarmerProfile(
                    name = GlobalMockData.farmerName.value,
                    phone = GlobalMockData.farmerPhone.value,
                    village = GlobalMockData.farmerVillage.value,
                    district = district.ifBlank { GlobalMockData.farmerDistrict.value },
                    state = state.ifBlank { GlobalMockData.farmerState.value },
                    latitude = GlobalMockData.farmerLatitude.value,
                    longitude = GlobalMockData.farmerLongitude.value,
                    crop = selectedCrop,
                    farmArea = finalArea,
                    areaUnit = finalUnit,
                    role = "FARMER"
                )

                // Establish authenticated persistent session
                val token = "jwt_farmer_${GlobalMockData.farmerPhone.value}_${System.currentTimeMillis()}"
                GlobalMockData.setSession(true, token, "FARMER")

                onAnalyze()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = AppStrings.analyzeMyFarmBtn,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
