package com.krushiadhaar.app.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.KrushiAdhaarApplication
import com.krushiadhaar.app.ui.theme.AppBackground
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.GreenSurface
import com.krushiadhaar.app.ui.theme.OrangePrimary
import com.krushiadhaar.app.ui.theme.TextPrimary
import com.krushiadhaar.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isFromMe: Boolean, val time: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    farmerName: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as KrushiAdhaarApplication
    val chatRepo = app.chatRepository
    val coroutineScope = rememberCoroutineScope()

    val dbMessages by chatRepo.getMessagesForConversation(farmerName).collectAsState(initial = emptyList())
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(farmerName) {
        // If starting a fresh chat, add initial opening message
        val current = app.database.chatMessageDao().getMessagesList(farmerName)
        if (current.isEmpty()) {
            chatRepo.sendMessage(
                conversationId = farmerName,
                peerName = farmerName,
                text = "Hello $farmerName, I am interested in negotiating for your crop listing.",
                isFromMe = true
            )
            delay(800)
            chatRepo.sendMessage(
                conversationId = farmerName,
                peerName = farmerName,
                text = "Namaskar! Glad to connect. What quantity and target delivery date are you planning?",
                isFromMe = false
            )
        }
    }

    LaunchedEffect(dbMessages.size) {
        if (dbMessages.isNotEmpty()) {
            listState.animateScrollToItem(dbMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = farmerName.firstOrNull()?.toString()?.uppercase() ?: "F",
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = farmerName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "● Online • Direct Negotiation",
                            color = GreenPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
            },
            actions = {
                val listing = com.krushiadhaar.app.GlobalMockData.marketplaceListings.find { it.farmer == farmerName }
                if (listing != null) {
                    TextButton(onClick = {
                        com.krushiadhaar.app.GlobalMockData.placeOrder(listing)
                        android.widget.Toast.makeText(context, "Order placed for ${listing.name}!", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Place Order", color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
        },
        containerColor = AppBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(dbMessages, key = { it.id }) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (msg.isFromMe) 16.dp else 4.dp,
                                            bottomEnd = if (msg.isFromMe) 4.dp else 16.dp
                                        )
                                    )
                                    .background(if (msg.isFromMe) GreenPrimary else Color.White)
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isFromMe) Color.White else TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (msg.timeFormatted.isNotBlank()) msg.timeFormatted else "Just now",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Bottom Message Input Field
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Write your proposal or reply...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (messageText.isNotBlank()) GreenPrimary else Color.LightGray)
                            .clickable(enabled = messageText.isNotBlank()) {
                                val textToSend = messageText.trim()
                                messageText = ""
                                coroutineScope.launch {
                                    chatRepo.sendMessage(
                                        conversationId = farmerName,
                                        peerName = farmerName,
                                        text = textToSend,
                                        isFromMe = true
                                    )
                                    // Contextual instant response from peer
                                    delay(1200)
                                    val replies = listOf(
                                        "Understood! We can schedule the truck arrival at your farm godown.",
                                        "We agree to the rate of ₹250 above mandi price. Please confirm batch availability.",
                                        "Could you share a moisture meter report or bag sample photos?",
                                        "Payment terms: 50% advance on weighing slip, balance upon delivery.",
                                        "Sounds great! We will proceed with placing the order on KrushiAdhaar."
                                    )
                                    chatRepo.sendMessage(
                                        conversationId = farmerName,
                                        peerName = farmerName,
                                        text = replies.random(),
                                        isFromMe = false
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
