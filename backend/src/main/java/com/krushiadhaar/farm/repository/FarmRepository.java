package com.krushiadhaar.farm.repository;
import com.krushiadhaar.farm.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmRepository extends JpaRepository<Farm, UUID> {
    List<Farm> findByOwnerIdAndStatus(UUID ownerId, String status);
    Optional<Farm> findByIdAndOwnerId(UUID id, UUID ownerId);
}
