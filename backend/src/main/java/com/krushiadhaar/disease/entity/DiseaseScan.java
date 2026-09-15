package com.krushiadhaar.disease.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disease_scans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseScan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "farm_id", nullable = false)
    private UUID farmId;
    @Column(name = "field_id", nullable = false)
    private UUID fieldId;
    @Column(name = "crop_cycle_id", nullable = false)
    private UUID cropCycleId;

    private String imageReference;
    @Builder.Default
    private String status = "PENDING";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
