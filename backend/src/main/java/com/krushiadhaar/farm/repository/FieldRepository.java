package com.krushiadhaar.farm.repository;
import com.krushiadhaar.farm.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
    List<Field> findByFarmIdAndStatus(UUID farmId, String status);
    Optional<Field> findByIdAndFarmOwnerId(UUID id, UUID ownerId);
}
