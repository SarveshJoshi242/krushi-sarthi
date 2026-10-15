package com.krushiadhaar.app.marketplace

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellCropScreen(
    onNavigateBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current

    var cropName by remember { mutableStateOf(GlobalMockData.farmerCrop.value) }
    var quantityText by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("Quintals") }
    var priceText by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf("Grade A (Premium)") }
    var farmLocation by remember { mutableStateOf(GlobalMockData.userLocation.value) }
    var notes by remember { mutableStateOf("Cleaned, harvested produce ready for prompt loading and transport.") }

    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            capturedBitmap = null
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            try {
                val file = File(context.cacheDir, "crop_pitch_${System.currentTimeMillis()}.jpg")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.flush()
                fos.close()
                selectedImageUri = file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Popular Crop Options
    val popularCrops = listOf(
        "Soyabean" to "🌱",
        "Wheat" to "🌾",
        "Cotton" to "🪴",
        "Sugarcane" to "🎋",
        "Rice" to "🌾",
        "Onion" to "🧅",
        "Tomato" to "🍅",
        "Maize" to "🌽"
    )

    // Derived Total Value Calculation
    val qtyNumber = quantityText.toDoubleOrNull() ?: 0.0
    val priceNumber = priceText.toIntOrNull() ?: 0
    val totalEstimatedValue = (qtyNumber * priceNumber).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pitch Crop for Sale",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Direct Mandi & Buyer Listing",
                            fontSize = 11.sp,
                            color = GreenDark
                        )
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .systemBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
            // Header Info Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GreenSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Direct Wholesale Access",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GreenDark
                        )
                        Text(
                            text = "List your harvest with photo proof to receive faster bids and premium market rates.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. CROP PHOTO PICKER CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "Crop Photo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        if (selectedImageUri != null || capturedBitmap != null) {
                            TextButton(
                                onClick = {
                                    selectedImageUri = null
                                    capturedBitmap = null
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Remove", color = RedError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (capturedBitmap != null || selectedImageUri != null) {
                        // Image Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, AppDivider, RoundedCornerShape(12.dp))
                                .background(Color(0xFFF9FBF9)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (capturedBitmap != null) {
                                Image(
                                    bitmap = capturedBitmap!!.asImageBitmap(),
                                    contentDescription = "Crop Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (selectedImageUri != null) {
                                val loadedBitmap = remember(selectedImageUri) {
                                    try {
                                        if (selectedImageUri!!.startsWith("content://") || selectedImageUri!!.startsWith("file://")) {
                                            val uri = Uri.parse(selectedImageUri)
                                            val stream = context.contentResolver.openInputStream(uri)
                                            BitmapFactory.decodeStream(stream)
                                        } else {
                                            BitmapFactory.decodeFile(selectedImageUri)
                                        }
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (loadedBitmap != null) {
                                    Image(
                                        bitmap = loadedBitmap.asImageBitmap(),
                                        contentDescription = "Crop Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("Image selected", color = GreenDark, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Change Photo Overlay Button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                                    .clickable { galleryLauncher.launch("image/*") }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Change Photo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    } else {
                        // Empty State Picker Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF6FAF6))
                                .border(1.5.dp, GreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Add crop photo to build buyer trust",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(
                                        onClick = { cameraLauncher.launch(null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Camera", fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { galleryLauncher.launch("image/*") },
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, GreenPrimary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = GreenDark, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gallery", fontSize = 12.sp, color = GreenDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. CROP INFORMATION FORM
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Crop Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Crop Selection Chips
                    Text(text = "Quick Select:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        popularCrops.forEach { (crop, emoji) ->
                            val isSelected = cropName.equals(crop, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) GreenPrimary else Color(0xFFF1F3F4),
                                modifier = Modifier.clickable { cropName = crop }
                            ) {
                                Text(
                                    text = "$emoji $crop",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cropName,
                        onValueChange = { cropName = it },
                        label = { Text("Crop Name & Variety") },
                        placeholder = { Text("e.g. Wheat (Lokwan), Soyabean (JS-335)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = AppDivider
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quantity and Unit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("Quantity") },
                            placeholder = { Text("e.g. 25") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = AppDivider
                            )
                        )

                        // Unit Selector
                        var unitExpanded by remember { mutableStateOf(false) }
                        val units = listOf("Quintals", "Tons", "Bags (50kg)", "Kg")

                        ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = !unitExpanded },
                            modifier = Modifier.weight(0.9f)
                        ) {
                            OutlinedTextField(
                                value = selectedUnit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Unit") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenPrimary,
                                    unfocusedBorderColor = AppDivider
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                units.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u) },
                                        onClick = {
                                            selectedUnit = u
                                            unitExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price Input
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Expected Price (₹ per $selectedUnit)") },
                        placeholder = { Text("e.g. 2600") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = AppDivider
                        )
                    )

                    // Live Total Calculation
                    if (totalEstimatedValue > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GreenSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Lot Value:",
                                    fontSize = 12.sp,
                                    color = GreenDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "₹ $totalEstimatedValue",
                                    fontSize = 15.sp,
                                    color = GreenDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quality Grade Selection
                    Text(text = "Quality Grade", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Grade A (Premium)", "Grade B", "Organic").forEach { grade ->
                            val isSel = selectedGrade == grade
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) GreenSurface else Color(0xFFF5F5F5),
                                border = if (isSel) BorderStroke(1.dp, GreenPrimary) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedGrade = grade }
                            ) {
                                Text(
                                    text = grade,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) GreenPrimary else TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Farm Location
                    OutlinedTextField(
                        value = farmLocation,
                        onValueChange = { farmLocation = it },
                        label = { Text("Farm Location / Mandi") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GreenPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = AppDivider
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (cropName.isBlank()) {
                        Toast.makeText(context, "Please enter crop name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val qty = quantityText.toDoubleOrNull() ?: 0.0
                    val price = priceText.toIntOrNull() ?: 0
                    if (qty <= 0.0 || price <= 0) {
                        Toast.makeText(context, "Please enter valid quantity and price", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Convert to Tons for standard marketplace display
                    val approxTons = when (selectedUnit) {
                        "Tons" -> qty.toInt().coerceAtLeast(1)
                        "Quintals" -> (qty / 10.0).toInt().coerceAtLeast(1)
                        "Bags (50kg)" -> ((qty * 50) / 1000.0).toInt().coerceAtLeast(1)
                        else -> 1
                    }

                    val pricePerTon = when (selectedUnit) {
                        "Tons" -> price
                        "Quintals" -> price * 10
                        "Bags (50kg)" -> price * 20
                        else -> price * 10
                    }

                    val cropEmoji = when {
                        cropName.contains("Wheat", true) || cropName.contains("गहू", true) -> "🌾"
                        cropName.contains("Rice", true) || cropName.contains("भात", true) -> "🌾"
                        cropName.contains("Cotton", true) || cropName.contains("कापूस", true) -> "🪴"
                        cropName.contains("Sugar", true) || cropName.contains("ऊस", true) -> "🎋"
                        cropName.contains("Onion", true) || cropName.contains("कांदा", true) -> "🧅"
                        cropName.contains("Tomato", true) || cropName.contains("टोमॅटो", true) -> "🍅"
                        cropName.contains("Maize", true) || cropName.contains("मका", true) -> "🌽"
                        else -> "🌱"
                    }

                    // Add to active listings and persist
                    GlobalMockData.addMarketplaceListing(
                        CropListing(
                            name = cropName.trim(),
                            farmer = "${GlobalMockData.farmerName.value} (You)",
                            location = farmLocation.trim(),
                            quantityTons = approxTons,
                            pricePerTon = pricePerTon,
                            emoji = cropEmoji,
                            emojiBg = Color(0xFFE8F5E9),
                            imageUri = selectedImageUri,
                            originalQuantity = qty,
                            originalUnit = selectedUnit,
                            originalPrice = price,
                            grade = selectedGrade
                        )
                    )

                    Toast.makeText(context, "🎉 $cropName pitched successfully to buyers!", Toast.LENGTH_LONG).show()
                    onSubmitSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "List on Marketplace",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancel", color = TextSecondary, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
}
