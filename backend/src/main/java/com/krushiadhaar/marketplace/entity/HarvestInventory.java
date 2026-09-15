package com.krushiadhaar.marketplace.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "harvest_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "harvest_id", nullable = false)
    private UUID harvestId;
    @Column(name = "crop_id", nullable = false)
    private UUID cropId;

    private BigDecimal quantityAvailable;
    private BigDecimal quantityReserved;
    private BigDecimal quantitySold;
    private String unit;
    private String qualityGrade;
    
    @Builder.Default
    private String status = "AVAILABLE";

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
