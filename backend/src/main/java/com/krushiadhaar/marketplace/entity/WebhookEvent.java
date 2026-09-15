package com.krushiadhaar.marketplace.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
@Entity
@Table(name = "webhook_events")
@Data
public class WebhookEvent {
    @Id
    private UUID id = UUID.randomUUID();
    private String provider;
    private String providerEventId;
    private String payload;
    private LocalDateTime processedAt = LocalDateTime.now();
}
