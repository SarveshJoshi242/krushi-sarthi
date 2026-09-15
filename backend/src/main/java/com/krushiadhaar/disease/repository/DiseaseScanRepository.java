package com.krushiadhaar.disease.repository;
import com.krushiadhaar.disease.entity.DiseaseScan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiseaseScanRepository extends JpaRepository<DiseaseScan, UUID> {
    List<DiseaseScan> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<DiseaseScan> findByIdAndUserId(UUID id, UUID userId);
}
