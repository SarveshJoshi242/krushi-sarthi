package com.krushiadhaar.marketplace.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "marketplace_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceListing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "seller_user_id", nullable = false)
    private UUID sellerUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private HarvestInventory inventory;

    @Column(name = "crop_id", nullable = false)
    private UUID cropId;

    private String title;
    private String description;
    private BigDecimal pricePerUnit;
    
    @Builder.Default
    private String status = "DRAFT";

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
