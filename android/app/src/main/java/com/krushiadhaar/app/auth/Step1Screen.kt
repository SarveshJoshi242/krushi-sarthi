package com.krushiadhaar.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.AppBackground
import com.krushiadhaar.app.ui.theme.GreenDark
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.GreenSurface
import com.krushiadhaar.app.ui.theme.TextPrimary
import com.krushiadhaar.app.ui.theme.TextSecondary
import com.krushiadhaar.app.util.AppStrings
import com.krushiadhaar.app.util.IndianLocation
import com.krushiadhaar.app.util.IndianLocationsCatalog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Screen(
    onNext: () -> Unit = {},
    onLoginAsFarmer: () -> Unit = {},
    onLoginAsBuyer: () -> Unit = {}
) {
    val context = LocalContext.current
    var isLoginMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(GlobalMockData.farmerName.value) }
    var phone by remember { mutableStateOf(GlobalMockData.farmerPhone.value) }
    var password by remember { mutableStateOf("") }
    var customVillage by remember { mutableStateOf(GlobalMockData.farmerVillage.value) }

    var selectedLocation by remember {
        mutableStateOf(
            IndianLocationsCatalog.locations.find { it.district.equals(GlobalMockData.farmerDistrict.value, true) }
                ?: IndianLocationsCatalog.getDefault()
        )
    }

    var showLocationDialog by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    var currentLang by remember { mutableStateOf(GlobalMockData.userLanguage.value) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Row: Title + Responsive Language Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = if (isLoginMode) AppStrings.signInSubtitle else AppStrings.step1Subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isLoginMode) AppStrings.welcomeBackTitle else AppStrings.accountDetailsTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Responsive Language Pill
            Box {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.clickable { languageMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentLang,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("English", fontWeight = if (currentLang == "English") FontWeight.Bold else FontWeight.Normal)
                                if (currentLang == "English") {
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.Check, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        onClick = {
                            currentLang = "English"
                            GlobalMockData.updateLanguage("English")
                            languageMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("हिंदी (Hindi)", fontWeight = if (currentLang == "हिंदी") FontWeight.Bold else FontWeight.Normal)
                                if (currentLang == "हिंदी") {
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.Check, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        onClick = {
                            currentLang = "हिंदी"
                            GlobalMockData.updateLanguage("हिंदी")
                            languageMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("मराठी (Marathi)", fontWeight = if (currentLang == "मराठी") FontWeight.Bold else FontWeight.Normal)
                                if (currentLang == "मराठी") {
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.Check, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        onClick = {
                            currentLang = "मराठी"
                            GlobalMockData.updateLanguage("मराठी")
                            languageMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        if (!isLoginMode) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text(AppStrings.nameLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = GreenPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = phone,
            onValueChange = { input ->
                if (input.length <= 10 && input.all { it.isDigit() }) {
                    phone = input
                    errorMessage = null
                }
            },
            label = { Text(AppStrings.phoneLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text(AppStrings.passwordLabel) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (!isLoginMode) {
            // Location Hub Dropdown Selector
            Text(
                text = AppStrings.locationSelectLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLocationDialog = true },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D7DE))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedLocation.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "APMC: ${selectedLocation.nearestMandi}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specific village text field
            OutlinedTextField(
                value = customVillage,
                onValueChange = { customVillage = it },
                label = { Text(AppStrings.customVillageLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = GreenPrimary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Error message if any
        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFFEBEE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFC62828),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // Toggle Sign In / Sign Up
        Text(
            text = if (isLoginMode) AppStrings.dontHaveAccountPrompt else AppStrings.alreadyHaveAccountPrompt,
            color = GreenPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable {
                    isLoginMode = !isLoginMode
                    errorMessage = null
                }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Main Action Button
        Button(
            onClick = {
                if (phone.length < 10) {
                    errorMessage = if (currentLang == "हिंदी") "कृपया 10 अंकों का मोबाइल नंबर दर्ज करें" else if (currentLang == "मराठी") "कृपया 10 अंकी मोबाईल नंबर टाका" else "Please enter a valid 10-digit mobile number"
                    return@Button
                }
                if (password.length < 4) {
                    errorMessage = if (currentLang == "हिंदी") "पासवर्ड कम से कम 4 अक्षरों का होना चाहिए" else if (currentLang == "मराठी") "पासवर्ड किमान 4 वर्णांचा असावा" else "Password must be at least 4 characters"
                    return@Button
                }

                if (isLoginMode) {
                    // Authenticate Farmer
                    val token = "jwt_farmer_${phone}_${System.currentTimeMillis()}"
                    GlobalMockData.farmerPhone.value = phone
                    GlobalMockData.setSession(true, token, "FARMER")
                    onLoginAsFarmer()
                } else {
                    if (name.isBlank()) {
                        errorMessage = if (currentLang == "हिंदी") "कृपया अपना नाम दर्ज करें" else if (currentLang == "मराठी") "कृपया आपले नाव टाका" else "Please enter your full name"
                        return@Button
                    }

                    // Save step 1 information
                    val finalVillage = if (customVillage.isNotBlank()) customVillage.trim() else selectedLocation.villageOrTaluka
                    GlobalMockData.farmerName.value = name.trim()
                    GlobalMockData.farmerPhone.value = phone.trim()
                    GlobalMockData.farmerVillage.value = finalVillage
                    GlobalMockData.farmerDistrict.value = selectedLocation.district
                    GlobalMockData.farmerState.value = selectedLocation.state
                    GlobalMockData.farmerLatitude.value = selectedLocation.latitude
                    GlobalMockData.farmerLongitude.value = selectedLocation.longitude
                    GlobalMockData.userLocation.value = "$finalVillage, ${selectedLocation.district} • ${selectedLocation.state}"

                    onNext()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isLoginMode) AppStrings.loginAsFarmerBtn else AppStrings.nextAddFarmBtn,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (isLoginMode) {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (phone.length < 10 || password.length < 4) {
                        errorMessage = "Please enter valid phone and password"
                        return@Button
                    }
                    val token = "jwt_buyer_${phone}_${System.currentTimeMillis()}"
                    GlobalMockData.setSession(true, token, "BUYER")
                    onLoginAsBuyer()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3F2FD),
                    contentColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = AppStrings.loginAsBuyerBtn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    // Indian Locations Selection Dialog
    if (showLocationDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredList = remember(searchQuery) { IndianLocationsCatalog.search(searchQuery) }

        Dialog(onDismissRequest = { showLocationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.locationSelectLabel,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = { showLocationDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(AppStrings.searchLocationHint, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = GreenPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList) { loc ->
                            val isSelected = loc.id == selectedLocation.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GreenSurface else Color(0xFFFAFAFA),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) GreenPrimary else Color(0xFFE0E0E0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLocation = loc
                                        showLocationDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) GreenPrimary else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = loc.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Mandi: ${loc.nearestMandi} • Crops: ${loc.primaryCrops.take(2).joinToString()}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = GreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
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
