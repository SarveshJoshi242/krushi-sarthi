package com.krushiadhaar.app.schemes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler

private val PrimaryGreen = Color(0xFF8AC149)
private val BackgroundColor = Color(0xFFF8F9FA)
private val DarkText = Color(0xFF212121)
private val GrayText = Color(0xFF757575)

data class Scheme(
    val title: String,
    val provider: String,
    val description: String,
    val isActive: Boolean,
    val link: String,
    val requiredDocuments: List<String> = emptyList(),
    val type: String = "Scheme"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernmentSchemesScreen(
    onBackClick: () -> Unit = {}
) {
    val isHindi = com.krushiadhaar.app.util.AppStrings.isHindi()
    val isMarathi = com.krushiadhaar.app.util.AppStrings.isMarathi()
    var schemes by remember { mutableStateOf<List<Scheme>>(getDefaultSchemes(isHindi, isMarathi)) }
    var isLoading by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val lang = com.krushiadhaar.app.GlobalMockData.userLanguage.value
        val loc = com.krushiadhaar.app.GlobalMockData.userLocation.value
        try {
            val response = com.krushiadhaar.app.presentation.FarmRetrofitClient.api.getGovernmentSchemes(language = lang, location = loc)
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                schemes = response.body()!!.map {
                    Scheme(
                        title = it.title,
                        provider = it.provider,
                        description = it.description,
                        isActive = it.isActive,
                        link = it.link,
                        requiredDocuments = it.requiredDocuments,
                        type = it.type
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    var showEligibilityDialog by remember { mutableStateOf(false) }
    var hasAadhaar by remember { mutableStateOf(false) }
    var hasLandRecord by remember { mutableStateOf(false) }
    var hasBankPassbook by remember { mutableStateOf(false) }
    var applyEligibilityFilter by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    val userDocs = mutableListOf<String>()
    if (hasAadhaar) userDocs.add("Aadhaar Card")
    if (hasLandRecord) userDocs.add("Land Record")
    if (hasBankPassbook) userDocs.add("Bank Passbook")

    val filteredSchemes = schemes.filter {
        val matchesCategory = selectedFilter == "All" || it.type == selectedFilter || it.provider.contains(selectedFilter)
        val matchesEligibility = !applyEligibilityFilter || it.requiredDocuments.all { doc -> userDocs.contains(doc) }
        matchesCategory && matchesEligibility
    }

    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schemes & Benefits", color = DarkText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor),
                actions = {
                    TextButton(onClick = { showHistoryDialog = true }) {
                        Text("History", color = PrimaryGreen)
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Explore schemes, insurance, and loans available for your benefit.",
                color = GrayText,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = { showEligibilityDialog = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(if (applyEligibilityFilter) "Eligibility Filter Active - Tap to Edit" else "Check My Eligibility based on Documents")
            }

            Row(modifier = Modifier.padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Scheme", "Insurance", "Loan").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryGreen,
                            labelColor = GrayText
                        )
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSchemes) { scheme ->
                        SchemeCard(scheme = scheme, onLearnMoreClick = { uriHandler.openUri(scheme.link) })
                    }
                }
            }

        if (showEligibilityDialog) {
            AlertDialog(
                onDismissRequest = { showEligibilityDialog = false },
                title = { Text("My Documents") },
                text = {
                    Column {
                        Text("Select the documents you have available to check your eligibility for schemes, loans, and insurance.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hasAadhaar, onCheckedChange = { hasAadhaar = it })
                            Text("Aadhaar Card")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hasLandRecord, onCheckedChange = { hasLandRecord = it })
                            Text("Land Record (7/12 etc.)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = hasBankPassbook, onCheckedChange = { hasBankPassbook = it })
                            Text("Bank Passbook")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { 
                        applyEligibilityFilter = true
                        showEligibilityDialog = false 
                    }) { Text("Check Eligibility") }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        applyEligibilityFilter = false
                        showEligibilityDialog = false 
                    }) { Text("Clear Filter") }
                }
            )
        }

        if (showHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showHistoryDialog = false },
                title = { Text("Scheme Application History") },
                text = {
                    Column {
                        Text("PM-Kisan Samman Nidhi", fontWeight = FontWeight.Bold)
                        Text("Status: Approved (Active)", color = PrimaryGreen, fontSize = 12.sp)
                        Text("Applied: 10 Jan 2026", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Soil Health Card Scheme", fontWeight = FontWeight.Bold)
                        Text("Status: Processing", color = Color(0xFFFF9800), fontSize = 12.sp)
                        Text("Applied: 02 Sep 2026", fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHistoryDialog = false }) { Text("Close") }
                }
            )
        }

}
    }
}

@Composable
fun SchemeCard(scheme: Scheme, onLearnMoreClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scheme.title,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = scheme.provider,
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
                if (scheme.isActive) {
                    Surface(
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Active",
                            color = PrimaryGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = scheme.description,
                color = GrayText,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onLearnMoreClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Learn More", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

fun getDefaultSchemes(isHindi: Boolean = false, isMarathi: Boolean = false): List<Scheme> {
    return listOf(
        Scheme(
            title = if (isHindi) "प्रधानमंत्री किसान सम्मान निधि (PM-KISAN)" else if (isMarathi) "पंतप्रधान किसान सन्मान निधी (PM-KISAN)" else "PM-KISAN Samman Nidhi",
            provider = "Central Government",
            description = if (isHindi) "सभी भूमिधारक किसान परिवारों को ₹6,000 प्रति वर्ष तीन समान किस्तों में प्रत्यक्ष आय सहायता।" else if (isMarathi) "सर्व शेतकरी कुटुंबांना दरवर्षी ₹६,००० तीन हप्त्यांमध्ये थेट खात्यात मदत." else "Financial benefit of Rs. 6,000 per year in three equal 4-monthly installments into Aadhaar seeded bank accounts.",
            isActive = true,
            link = "https://pmkisan.gov.in/",
            requiredDocuments = listOf("Aadhaar Card", "Land Record", "Bank Passbook"),
            type = "Scheme"
        ),
        Scheme(
            title = if (isHindi) "प्रधानमंत्री फसल बीमा योजना (PMFBY)" else if (isMarathi) "पंतप्रधान पीक विमा योजना (PMFBY)" else "Pradhan Mantri Fasal Bima Yojana (PMFBY)",
            provider = "Ministry of Agriculture",
            description = if (isHindi) "प्राकृतिक आपदाओं, कीटों और बीमारियों के कारण फसल नुकसान से वित्तीय सुरक्षा प्रदान करने वाली व्यापक बीमा योजना।" else if (isMarathi) "नैसर्गिक आपत्ती, रोग व किडीमुळे होणाऱ्या पीक नुकसानीसाठी सर्वसमावेशक पीक विमा संरक्षण." else "Comprehensive crop insurance cover against non-preventable natural risks, drought, floods, pests and diseases.",
            isActive = true,
            link = "https://pmfby.gov.in/",
            requiredDocuments = listOf("Aadhaar Card", "Land Record", "Bank Passbook"),
            type = "Insurance"
        ),
        Scheme(
            title = if (isHindi) "पीएम-कुसुम सौर पंप योजना (PM-KUSUM)" else if (isMarathi) "पीएम-कुसुम सौर ऊर्जा पंप योजना (PM-KUSUM)" else "PM-KUSUM Solar Irrigation Scheme",
            provider = "MNRE & State Ag",
            description = if (isHindi) "खेतों में 3HP से 7.5HP सौर ऊर्जा वाटर पंप लगाने के लिए 60% से 90% तक सरकारी अनुदान/सब्सिडी।" else if (isMarathi) "शेतात सौर कृषी पंप बसवण्यासाठी ६०% ते ९०% पर्यंत सरकारी अनुदान." else "Subsidies up to 60-90% to set up standalone off-grid solar agricultural water pumps for uninterrupted daytime irrigation.",
            isActive = true,
            link = "https://pmkusum.mnre.gov.in/",
            requiredDocuments = listOf("Aadhaar Card", "Land Record"),
            type = "Scheme"
        ),
        Scheme(
            title = if (isHindi) "किसान क्रेडिट कार्ड (KCC Loan)" else if (isMarathi) "किसान क्रेडिट कार्ड (KCC कर्ज योजना)" else "Kisan Credit Card (KCC Loan)",
            provider = "NABARD & Public Banks",
            description = if (isHindi) "कृषि निवेशों (बीज, उर्वरक, कीटनाशक) के लिए 4% रियायती ब्याज दर पर ₹3 लाख तक का आसान ऋण।" else if (isMarathi) "बियाणे, खते आणि शेती खर्चासाठी ४% सवलतीच्या व्याजदरात ₹३ लाखांपर्यंत अल्पमुदत कर्ज." else "Timely credit for cultivation, seed/fertilizer inputs, and post-harvest expenses at 4% subvention interest rate up to Rs 3 Lakh.",
            isActive = true,
            link = "https://www.pmkisan.gov.in/KCC.aspx",
            requiredDocuments = listOf("Aadhaar Card", "Land Record", "Bank Passbook"),
            type = "Loan"
        ),
        Scheme(
            title = if (isHindi) "मृदा स्वास्थ्य कार्ड योजना (Soil Health Card)" else if (isMarathi) "मृदा आरोग्य पत्रिका योजना (Soil Health Card)" else "Soil Health Card Scheme",
            provider = "Dept of Agriculture & Farmers Welfare",
            description = if (isHindi) "मिट्टी की 12 रासायनिक व जैविक जांच के आधार पर फसलवार संतुलित उर्वरक सिफारिशें।" else if (isMarathi) "माती परीक्षणावर आधारित १२ घटकांची तपासणी आणि पिकांनुसार खतांचे संतुलित नियोजन." else "Assessment of 12 soil nutrient parameters with tailored fertilizer and micronutrient advisory to reduce input cost.",
            isActive = true,
            link = "https://soilhealth.dac.gov.in/",
            requiredDocuments = listOf("Aadhaar Card"),
            type = "Scheme"
        )
    )
}
