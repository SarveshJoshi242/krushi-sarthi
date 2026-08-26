package com.krushiadhaar.crop.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "crop_expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_cycle_id", nullable = false)
    private CropCycle cropCycle;

    private String category;
    private String description;
    private BigDecimal amount;
    @Builder.Default
    private String currency = "INR";
    private LocalDate expenseDate;
    private String notes;

    @Version
    private Long version;
}
