package com.krushiadhaar.crop.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "crop_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_cycle_id", nullable = false)
    private CropCycle cropCycle;

    private String title;
    private String description;
    private String activityType;
    private LocalDate scheduledDate;
    private LocalDateTime completedAt;
    
    @Builder.Default
    private String status = "PENDING";
    private String notes;

    @Version
    private Long version;
}
