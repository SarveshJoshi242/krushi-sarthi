package com.krushiadhaar.app.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit = {}, onLogout: () -> Unit = {}) {
    val context = LocalContext.current
    var currentLang by remember { mutableStateOf(GlobalMockData.userLanguage.value) }
    val isMr = currentLang.contains("Marathi", true) || currentLang.contains("मराठी", true)
    val isHi = currentLang.contains("Hindi", true) || currentLang.contains("हिंदी", true)

    var showEditDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Farmer Profile Local View States
    val nameState = GlobalMockData.farmerName.value
    val phoneState = GlobalMockData.farmerPhone.value
    val villageState = GlobalMockData.farmerVillage.value
    val stateVal = GlobalMockData.farmerState.value
    val districtVal = GlobalMockData.farmerDistrict.value
    val cropVal = GlobalMockData.farmerCrop.value
    val areaVal = GlobalMockData.farmerFarmArea.value
    val unitVal = GlobalMockData.farmerAreaUnit.value
    val soilVal = GlobalMockData.farmerSoilType.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Dark Green Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = GreenDark,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(top = 40.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMr) "माझे प्रोफाईल" else if (isHi) "मेरा प्रोफाइल" else "Profile",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(GreenDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(40.dp))
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = nameState,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isMr) "प्रमाणित शेतकरी" else if (isHi) "सत्यापित किसान" else "Verified Farmer",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$villageState, $districtVal • $stateVal",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Farmer Details Card with Edit Option
        ProfileSectionCard(
            title = if (isMr) "शेतकरी तपशील" else if (isHi) "किसान विवरण" else "Farmer Details",
            icon = Icons.Default.PersonOutline,
            action = {
                TextButton(
                    onClick = { showEditDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMr) "बदला" else if (isHi) "बदलें" else "Edit",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        ) {
            ProfileRow(label = if (isMr) "नाव" else if (isHi) "नाम" else "Name", value = nameState)
            ProfileRow(label = if (isMr) "मोबाईल नंबर" else if (isHi) "फ़ोन नंबर" else "Phone Number", value = phoneState)
            ProfileRow(label = if (isMr) "गाव" else if (isHi) "गाँव" else "Village", value = villageState, showDivider = false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Farm Information Card with Edit Option
        ProfileSectionCard(
            title = if (isMr) "शेती तपशील" else if (isHi) "खेत विवरण" else "Farm Information",
            icon = Icons.Default.Agriculture,
            action = {
                TextButton(
                    onClick = { showEditDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMr) "बदला" else if (isHi) "बदलें" else "Edit",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        ) {
            ProfileRow(label = if (isMr) "राज्य" else if (isHi) "राज्य" else "State", value = stateVal)
            ProfileRow(label = if (isMr) "जिल्हा" else if (isHi) "ज़िला" else "District", value = districtVal)
            ProfileRow(label = if (isMr) "मुख्य पीक" else if (isHi) "प्रमुख फसल" else "Crop Type", value = cropVal)
            ProfileRow(label = if (isMr) "जमीन क्षेत्र" else if (isHi) "खेत का रकबा" else "Farm Area", value = "$areaVal $unitVal")
            ProfileRow(label = if (isMr) "मातीचा प्रकार" else if (isHi) "मिट्टी का प्रकार" else "Soil Type", value = soilVal, showDivider = false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferred Language Card (Working Selector)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showLanguageDialog = true },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Language, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMr) "पसंतीची भाषा" else if (isHi) "पसंदीदा भाषा" else "Preferred Language",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = when {
                            currentLang.contains("Hindi", true) || currentLang.contains("हिंदी", true) -> "हिंदी (Hindi)"
                            currentLang.contains("Marathi", true) || currentLang.contains("मराठी", true) -> "मराठी (Marathi)"
                            else -> "English"
                        },
                        fontSize = 12.sp,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable(onClick = onLogout),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = RedError, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isMr) "बाहेर पडा (Sign Out)" else if (isHi) "लॉग आउट करें (Sign Out)" else "Sign Out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Account Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showDeleteDialog = true },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RedError, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isMr) "खाते हटवा (Delete Account)" else if (isHi) "खाता हटाएं (Delete Account)" else "Delete Account",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedError,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = RedError)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

    // ==========================================
    // EDIT PROFILE DIALOG
    // ==========================================
    if (showEditDialog) {
        var editName by remember { mutableStateOf(nameState) }
        var editPhone by remember { mutableStateOf(phoneState) }
        var editVillage by remember { mutableStateOf(villageState) }
        var editDistrict by remember { mutableStateOf(districtVal) }
        var editState by remember { mutableStateOf(stateVal) }
        var editCrop by remember { mutableStateOf(cropVal) }
        var editArea by remember { mutableStateOf(areaVal) }
        var editUnit by remember { mutableStateOf(unitVal) }
        var editSoil by remember { mutableStateOf(soilVal) }

        var cropDropdownExpanded by remember { mutableStateOf(false) }
        val cropList = listOf(
            "Sugarcane", "Cotton", "Soyabean", "Wheat", "Rice",
            "Pumpkin", "Tomato", "Onion", "Maize", "Potato",
            "Brinjal", "Groundnut", "Chilli", "Bitter Gourd", "Ridge Gourd"
        )

        var soilDropdownExpanded by remember { mutableStateOf(false) }
        val soilList = listOf(
            "Optimal Fertile Loam",
            "Black Cotton Soil (Regur)",
            "Red Sandy Loam",
            "Alluvial Clay Loam",
            "Laterite Well Drained Soil"
        )

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = if (isMr) "शेतकरी माहिती संपादित करा" else if (isHi) "किसान जानकारी संपादित करें" else "Edit Farmer Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (isMr) "शेतकऱ्याचे नाव" else if (isHi) "किसान का नाम" else "Farmer Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text(if (isMr) "मोबाईल नंबर" else if (isHi) "फ़ोन नंबर" else "Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editVillage,
                        onValueChange = { editVillage = it },
                        label = { Text(if (isMr) "गाव" else if (isHi) "गाँव" else "Village") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editDistrict,
                            onValueChange = { editDistrict = it },
                            label = { Text(if (isMr) "जिल्हा" else if (isHi) "ज़िला" else "District") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editState,
                            onValueChange = { editState = it },
                            label = { Text(if (isMr) "राज्य" else if (isHi) "राज्य" else "State") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Crop Selection Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = editCrop,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isMr) "मुख्य पीक" else if (isHi) "प्रमुख फसल" else "Main Crop") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { cropDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = cropDropdownExpanded,
                            onDismissRequest = { cropDropdownExpanded = false }
                        ) {
                            cropList.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        editCrop = c
                                        cropDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Farm Area & Unit
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editArea,
                            onValueChange = { editArea = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text(if (isMr) "क्षेत्र" else if (isHi) "रकबा" else "Area") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editUnit,
                            onValueChange = { editUnit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Soil Type Selection
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = editSoil,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isMr) "मातीचा प्रकार" else if (isHi) "मिट्टी का प्रकार" else "Soil Type") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { soilDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = soilDropdownExpanded,
                            onDismissRequest = { soilDropdownExpanded = false }
                        ) {
                            soilList.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        editSoil = s
                                        soilDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank() || editPhone.isBlank()) {
                            Toast.makeText(context, "Please fill in name and phone", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        GlobalMockData.saveFarmerProfile(
                            name = editName.trim(),
                            phone = editPhone.trim(),
                            village = editVillage.trim(),
                            district = editDistrict.trim(),
                            state = editState.trim(),
                            latitude = GlobalMockData.farmerLatitude.value,
                            longitude = GlobalMockData.farmerLongitude.value,
                            crop = editCrop.trim(),
                            farmArea = editArea.trim(),
                            areaUnit = editUnit.trim(),
                            soilType = editSoil.trim()
                        )
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(if (isMr) "जतन करा" else if (isHi) "सहेजें" else "Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(if (isMr) "रद्द करा" else if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    // ==========================================
    // LANGUAGE SELECTION DIALOG
    // ==========================================
    if (showLanguageDialog) {
        val languages = listOf(
            "English" to "English",
            "Hindi" to "हिंदी (Hindi)",
            "Marathi" to "मराठी (Marathi)"
        )

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = if (isMr) "भाषा निवडा" else if (isHi) "भाषा चुनें" else "Select Language",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    languages.forEach { (langCode, langName) ->
                        val isSelected = currentLang.contains(langCode, ignoreCase = true)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentLang = langCode
                                    GlobalMockData.updateLanguage(langCode)
                                    Toast.makeText(context, "Language changed to $langName", Toast.LENGTH_SHORT).show()
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    currentLang = langCode
                                    GlobalMockData.updateLanguage(langCode)
                                    Toast.makeText(context, "Language changed to $langName", Toast.LENGTH_SHORT).show()
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = langName,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GreenPrimary else TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(if (isMr) "बंद करा" else if (isHi) "बंद करें" else "Close")
                }
            }
        )
    }

    // ==========================================
    // DELETE ACCOUNT DIALOG
    // ==========================================
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(if (isMr) "खाते हटवा" else if (isHi) "खाता हटाएं" else "Delete Account", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete your account? All your local farm data, preferences, and session tokens will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onLogout()
                }) {
                    Text("Delete", color = RedError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                action?.invoke()
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String, showDivider: Boolean = true) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = TextSecondary, fontSize = 14.sp)
            Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        if (showDivider) {
            Divider(color = AppDivider, thickness = 0.5.dp)
        }
    }
}
