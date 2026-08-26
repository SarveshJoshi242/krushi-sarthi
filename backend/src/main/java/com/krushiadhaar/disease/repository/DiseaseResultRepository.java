package com.krushiadhaar.disease.repository;
import com.krushiadhaar.disease.entity.DiseaseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DiseaseResultRepository extends JpaRepository<DiseaseResult, UUID> {
    Optional<DiseaseResult> findByScanId(UUID scanId);
}
