package com.krushiadhaar.chat.repository;

import com.krushiadhaar.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(UUID chatRoomId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoomId = :roomId AND m.senderId != :userId AND m.isRead = false")
    long countUnreadMessages(UUID roomId, UUID userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoomId = :roomId AND m.senderId != :userId AND m.isRead = false")
    void markAsRead(UUID roomId, UUID userId);
}
