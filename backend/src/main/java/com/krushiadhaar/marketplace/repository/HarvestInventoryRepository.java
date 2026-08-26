package com.krushiadhaar.marketplace.repository;
import com.krushiadhaar.marketplace.entity.HarvestInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface HarvestInventoryRepository extends JpaRepository<HarvestInventory, UUID> {
    
    // Pessimistic Write Lock for Inventory Reservation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM HarvestInventory i WHERE i.id = :id")
    Optional<HarvestInventory> findByIdForUpdate(UUID id);
}
