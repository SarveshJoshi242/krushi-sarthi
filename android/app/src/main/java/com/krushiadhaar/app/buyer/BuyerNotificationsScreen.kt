package com.krushiadhaar.app.buyer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.*

data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType
)

enum class NotificationType {
    NEW_CROP, MESSAGE, MARKET_TREND
}

private val sampleNotifications = listOf(
    NotificationItem(
        "New Crop in Market",
        "Ramesh Patil just listed 10 Tons of Sugarcane in Pune.",
        "2 mins ago",
        NotificationType.NEW_CROP
    ),
    NotificationItem(
        "Market Trend Alert",
        "High demand for Wheat in your area. Market average price is ₹2,600/Ton.",
        "1 hour ago",
        NotificationType.MARKET_TREND
    ),
    NotificationItem(
        "Message from Farmer",
        "Suresh: Yes, I can deliver it by tomorrow.",
        "3 hours ago",
        NotificationType.MESSAGE
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerNotificationsScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleNotifications) { notif ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = when(notif.type) {
                                NotificationType.NEW_CROP -> Icons.Default.Info
                                NotificationType.MESSAGE -> Icons.Default.Message
                                NotificationType.MARKET_TREND -> Icons.Default.TrendingUp
                            },
                            contentDescription = null,
                            tint = when(notif.type) {
                                NotificationType.NEW_CROP -> GreenPrimary
                                NotificationType.MESSAGE -> OrangePrimary
                                NotificationType.MARKET_TREND -> Color(0xFF1976D2)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = notif.message, fontSize = 13.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = notif.time, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
