package com.krushiadhaar.chat.service;

import com.krushiadhaar.chat.dto.ChatRoomDto;
import com.krushiadhaar.chat.entity.ChatMessage;
import com.krushiadhaar.chat.entity.ChatRoom;
import com.krushiadhaar.chat.repository.ChatMessageRepository;
import com.krushiadhaar.chat.repository.ChatRoomRepository;
import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.user.entity.User;
import com.krushiadhaar.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public ChatRoom getOrCreateRoom(UUID listingId, UUID buyerId, UUID sellerId) {
        return chatRoomRepository.findByListingIdAndBuyerIdAndSellerId(listingId, buyerId, sellerId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .listingId(listingId)
                            .buyerId(buyerId)
                            .sellerId(sellerId)
                            .build();
                    return chatRoomRepository.save(room);
                });
    }

    public List<ChatRoomDto> getUserChatRooms(UUID userId) {
        List<ChatRoom> rooms = chatRoomRepository.findByUserId(userId);
        return rooms.stream().map(room -> {
            String buyerName = userRepository.findById(room.getBuyerId())
                    .map(User::getFullName).orElse("Unknown");
            String sellerName = userRepository.findById(room.getSellerId())
                    .map(User::getFullName).orElse("Unknown");

            List<ChatMessage> messages = chatMessageRepository
                    .findByChatRoomIdOrderByCreatedAtAsc(room.getId());
            String lastMsg = messages.isEmpty() ? "" :
                    messages.get(messages.size() - 1).getMessage();

            long unread = chatMessageRepository.countUnreadMessages(room.getId(), userId);

            return ChatRoomDto.builder()
                    .id(room.getId())
                    .listingId(room.getListingId())
                    .buyerId(room.getBuyerId())
                    .sellerId(room.getSellerId())
                    .buyerName(buyerName)
                    .sellerName(sellerName)
                    .lastMessage(lastMsg)
                    .unreadCount(unread)
                    .status(room.getStatus())
                    .createdAt(room.getCreatedAt())
                    .updatedAt(room.getUpdatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    public ChatMessage sendMessage(UUID chatRoomId, UUID senderId, String message, String messageType) {
        ChatRoom room = chatRoomRepository.findByIdAndUserId(chatRoomId, senderId)
                .orElseThrow(() -> new UnauthorizedResourceAccessException("Chat room not found or access denied"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .message(message)
                .messageType(messageType != null ? messageType : "TEXT")
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getRoomMessages(UUID chatRoomId, UUID userId) {
        chatRoomRepository.findByIdAndUserId(chatRoomId, userId)
                .orElseThrow(() -> new UnauthorizedResourceAccessException("Chat room not found or access denied"));

        // Mark messages as read
        chatMessageRepository.markAsRead(chatRoomId, userId);

        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }
}
