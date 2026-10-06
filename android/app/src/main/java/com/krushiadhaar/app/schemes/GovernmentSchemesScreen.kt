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
    val link: String
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
            link = "https://pmfby.gov.in/"
        ),
        Scheme(
            title = "PM-Kisan Samman Nidhi",
            provider = "Central Government",
            description = "Provides income support of ₹6,000 per year to all landholding farmer families.",
            isActive = true,
            link = "https://pmkisan.gov.in/"
        ),
        Scheme(
            title = "MahaDBT Farmer Schemes",
            provider = "State Government",
            description = "Maharashtra direct benefit transfer for agriculture mechanization and irrigation.",
            isActive = true,
            link = "https://mahadbt.maharashtra.gov.in/"
        ),
        Scheme(
            title = "Soil Health Card Scheme",
            provider = "Central Government",
            description = "Promotes soil test based nutrient management.",
            isActive = true,
            link = "https://soilhealth.dac.gov.in/"
        )
    )

    var selectedFilter by remember { mutableStateOf("All") }
    val filteredSchemes = if (selectedFilter == "All") schemes else schemes.filter { it.provider.contains(selectedFilter) }
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Government Schemes", color = DarkText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
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
                text = "Explore schemes available for your benefit and apply to secure your farm and income.",
                color = GrayText,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                FilterChip(
                    selected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    label = { Text("All Schemes") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGreen
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = selectedFilter == "Central",
                    onClick = { selectedFilter = "Central" },
                    label = { Text("Central Government") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGreen,
                        labelColor = GrayText
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = selectedFilter == "State",
                    onClick = { selectedFilter = "State" },
                    label = { Text("State Government") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGreen,
                        labelColor = GrayText
                    )
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredSchemes) { scheme ->
                    SchemeCard(scheme = scheme, onLearnMoreClick = { uriHandler.openUri(scheme.link) })
                }
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
