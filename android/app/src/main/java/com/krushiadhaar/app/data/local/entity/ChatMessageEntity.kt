package com.krushiadhaar.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String, // identifies thread, e.g. peer name
    val peerName: String,
    val peerRole: String = "Buyer", // "Buyer" or "Farmer"
    val cropName: String = "",
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = ""
)
