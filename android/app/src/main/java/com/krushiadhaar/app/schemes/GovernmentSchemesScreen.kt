package com.krushiadhaar.app.schemes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    val schemes = listOf(
        Scheme(
            title = "Pradhan Mantri Fasal Bima Yojana",
            provider = "Central Government",
            description = "Crop insurance scheme to provide financial support to farmers in case of crop failure.",
            isActive = true,
            link = "https://pmfby.gov.in/",
            requiredDocuments = listOf("Aadhaar Card", "Land Record"),
            type = "Insurance"
        ),
        Scheme(
            title = "Kisan Credit Card (KCC) Loan",
            provider = "Banks",
            description = "Short term agricultural loans at subsidized interest rates.",
            isActive = true,
            link = "https://www.rbi.org.in/",
            requiredDocuments = listOf("Aadhaar Card", "Land Record", "Bank Passbook"),
            type = "Loan"
        ),
        Scheme(
            title = "PM-Kisan Samman Nidhi",
            provider = "Central Government",
            description = "Provides income support of ₹6,000 per year to all landholding farmer families.",
            isActive = true,
            link = "https://pmkisan.gov.in/",
            requiredDocuments = listOf("Aadhaar Card", "Bank Passbook"),
            type = "Scheme"
        ),
        Scheme(
            title = "MahaDBT Farmer Schemes",
            provider = "State Government",
            description = "Maharashtra direct benefit transfer for agriculture mechanization and irrigation.",
            isActive = true,
            link = "https://mahadbt.maharashtra.gov.in/",
            requiredDocuments = listOf("Aadhaar Card"),
            type = "Scheme"
        ),
        Scheme(
            title = "Soil Health Card Scheme",
            provider = "Central Government",
            description = "Promotes soil test based nutrient management.",
            isActive = true,
            link = "https://soilhealth.dac.gov.in/",
            requiredDocuments = emptyList(),
            type = "Scheme"
        )
    )

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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredSchemes) { scheme ->
                    SchemeCard(scheme = scheme, onLearnMoreClick = { uriHandler.openUri(scheme.link) })
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
