package com.krushiadhaar.chat.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateChatRoomRequest {
    private UUID listingId;
    private UUID sellerId;
}
