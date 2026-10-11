package com.krushiadhaar.chat.controller;

import com.krushiadhaar.chat.dto.ChatRoomDto;
import com.krushiadhaar.chat.dto.CreateChatRoomRequest;
import com.krushiadhaar.chat.entity.ChatMessage;
import com.krushiadhaar.chat.entity.ChatRoom;
import com.krushiadhaar.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createOrGetRoom(@RequestBody CreateChatRoomRequest request) {
        ChatRoom room = chatService.getOrCreateRoom(
                request.getListingId(), getUserId(), request.getSellerId());
        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getUserRooms() {
        return ResponseEntity.ok(chatService.getUserChatRooms(getUserId()));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable UUID roomId) {
        return ResponseEntity.ok(chatService.getRoomMessages(roomId, getUserId()));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable UUID roomId,
                                                    @RequestBody com.krushiadhaar.chat.dto.ChatMessageDto dto) {
        ChatMessage msg = chatService.sendMessage(roomId, getUserId(), dto.getMessage(), dto.getMessageType());
        return ResponseEntity.ok(msg);
    }
}
