package com.krushiadhaar.app.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.AppBackground
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.OrangePrimary
import com.krushiadhaar.app.ui.theme.TextPrimary

data class ChatMessage(val text: String, val isFromMe: Boolean, val time: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    farmerName: String,
    onNavigateBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    
    // Mock initial messages
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hello $farmerName, I am interested in your crop.", true, "10:00 AM"),
            ChatMessage("Hi! Sure, how much quantity are you looking for?", false, "10:05 AM")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(farmerName.firstOrNull()?.toString() ?: "F", color = GreenPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(farmerName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
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
                        val ctx = androidx.compose.ui.platform.LocalContext.current
                        TextButton(onClick = {
                            com.krushiadhaar.app.GlobalMockData.placeOrder(listing)
                            android.widget.Toast.makeText(ctx, "Order placed from chat!", android.widget.Toast.LENGTH_SHORT).show()
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start
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
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = msg.time, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Bottom Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                            .padding(12.dp)
                            .clickable {
                                if (messageText.isNotBlank()) {
                                    messages.add(ChatMessage(messageText, true, "Just now"))
                                    messageText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}
