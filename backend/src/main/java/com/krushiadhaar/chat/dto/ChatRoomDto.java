package com.krushiadhaar.chat.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChatRoomDto {
    private UUID id;
    private UUID listingId;
    private UUID buyerId;
    private UUID sellerId;
    private String buyerName;
    private String sellerName;
    private String lastMessage;
    private long unreadCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
