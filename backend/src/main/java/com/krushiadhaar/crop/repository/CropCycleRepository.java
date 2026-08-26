package com.krushiadhaar.crop.repository;
import com.krushiadhaar.crop.entity.CropCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CropCycleRepository extends JpaRepository<CropCycle, UUID> {
    List<CropCycle> findByFieldIdAndStatus(UUID fieldId, String status);
    Optional<CropCycle> findByIdAndFieldFarmOwnerId(UUID id, UUID ownerId);
}
