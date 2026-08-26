package com.krushiadhaar.marketplace.repository;
import com.krushiadhaar.marketplace.entity.MarketplaceListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MarketplaceListingRepository extends JpaRepository<MarketplaceListing, UUID> {
    Optional<MarketplaceListing> findByIdAndStatus(UUID id, String status);
}
