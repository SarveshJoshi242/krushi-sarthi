package com.krushiadhaar.chat.controller;

import com.krushiadhaar.chat.dto.ChatMessageDto;
import com.krushiadhaar.chat.entity.ChatMessage;
import com.krushiadhaar.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId,
                            @Payload ChatMessageDto dto,
                            Principal principal) {
        UUID senderId = UUID.fromString(principal.getName());
        UUID chatRoomId = UUID.fromString(roomId);

        ChatMessage saved = chatService.sendMessage(chatRoomId, senderId, dto.getMessage(), dto.getMessageType());

        // Broadcast to the room
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, saved);
    }
}
