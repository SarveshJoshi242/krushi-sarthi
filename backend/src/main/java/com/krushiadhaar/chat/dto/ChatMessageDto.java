package com.krushiadhaar.chat.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ChatMessageDto {
    private UUID chatRoomId;
    private String message;
    private String messageType;
}
