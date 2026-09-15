package com.krushiadhaar.crop.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "harvests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Harvest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_cycle_id", nullable = false)
    private CropCycle cropCycle;

    private LocalDate harvestDate;
    private BigDecimal quantity;
    private String unit;
    private String quality;
    private BigDecimal sellingPrice;
    private BigDecimal totalRevenue;
    private String notes;

    @Version
    private Long version;
}
