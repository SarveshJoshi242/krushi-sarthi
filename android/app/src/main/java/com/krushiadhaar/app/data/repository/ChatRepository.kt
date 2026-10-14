package com.krushiadhaar.app.data.repository

import com.krushiadhaar.app.data.local.dao.ChatMessageDao
import com.krushiadhaar.app.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    fun getMessagesForConversation(convId: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesForConversation(convId)
    }

    fun getAllMessages(): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getAllMessages()
    }

    suspend fun sendMessage(
        conversationId: String,
        peerName: String,
        text: String,
        isFromMe: Boolean,
        peerRole: String = "Buyer",
        cropName: String = ""
    ): ChatMessageEntity {
        val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val msg = ChatMessageEntity(
            conversationId = conversationId,
            peerName = peerName,
            peerRole = peerRole,
            cropName = cropName,
            text = text,
            isFromMe = isFromMe,
            timestamp = System.currentTimeMillis(),
            timeFormatted = timeFormatted
        )
        chatMessageDao.insertMessage(msg)
        return msg
    }

    suspend fun seedInitialConversationsIfEmpty() {
        if (chatMessageDao.countMessages() == 0) {
            val now = System.currentTimeMillis()
            val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
            
            val initial = listOf(
                ChatMessageEntity(
                    conversationId = "Ramesh Traders (APMC)",
                    peerName = "Ramesh Traders (APMC)",
                    peerRole = "Wholesale Buyer",
                    cropName = "Soyabean (JS-335)",
                    text = "Hello! We are interested in purchasing 35 Quintals of Soyabean from your listing.",
                    isFromMe = false,
                    timestamp = now - 3600000 * 2,
                    timeFormatted = timeFmt.format(Date(now - 3600000 * 2))
                ),
                ChatMessageEntity(
                    conversationId = "Ramesh Traders (APMC)",
                    peerName = "Ramesh Traders (APMC)",
                    peerRole = "Wholesale Buyer",
                    cropName = "Soyabean (JS-335)",
                    text = "Can you do ₹4,650 per Quintal with farm pickup on Monday?",
                    isFromMe = false,
                    timestamp = now - 3600000,
                    timeFormatted = timeFmt.format(Date(now - 3600000))
                ),
                ChatMessageEntity(
                    conversationId = "Priya Agro Mandi",
                    peerName = "Priya Agro Mandi",
                    peerRole = "Certified Trader",
                    cropName = "Wheat (Lokwan)",
                    text = "Namaskar! Is the Lokwan Grade A Wheat still available for dispatch?",
                    isFromMe = false,
                    timestamp = now - 1800000,
                    timeFormatted = timeFmt.format(Date(now - 1800000))
                ),
                ChatMessageEntity(
                    conversationId = "Priya Agro Mandi",
                    peerName = "Priya Agro Mandi",
                    peerRole = "Certified Trader",
                    cropName = "Wheat (Lokwan)",
                    text = "Yes, 20 Quintals are cleaned, bagged, and ready in godown.",
                    isFromMe = true,
                    timestamp = now - 900000,
                    timeFormatted = timeFmt.format(Date(now - 900000))
                )
            )
            chatMessageDao.insertMessages(initial)
        }
    }
}
