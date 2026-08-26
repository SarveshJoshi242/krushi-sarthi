package com.krushiadhaar.disease.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "disease_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scan_id", nullable = false)
    private UUID scanId;

    private String diseaseName;
    private BigDecimal confidence;
    private String severity;
    private String recommendation;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
