package com.krushiadhaar.marketplace.repository;
import com.krushiadhaar.marketplace.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdempotencyKeyAndBuyerUserId(UUID idempotencyKey, UUID buyerUserId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(UUID id);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.paymentDeadline <= :now")
    List<Order> findExpiredCandidates(String status, LocalDateTime now);
}
