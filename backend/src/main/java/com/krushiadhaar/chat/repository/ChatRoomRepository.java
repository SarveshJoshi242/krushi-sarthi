package com.krushiadhaar.chat.repository;

import com.krushiadhaar.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    @Query("SELECT c FROM ChatRoom c WHERE c.buyerId = :userId OR c.sellerId = :userId ORDER BY c.updatedAt DESC")
    List<ChatRoom> findByUserId(UUID userId);

    Optional<ChatRoom> findByListingIdAndBuyerIdAndSellerId(UUID listingId, UUID buyerId, UUID sellerId);

    @Query("SELECT c FROM ChatRoom c WHERE c.id = :roomId AND (c.buyerId = :userId OR c.sellerId = :userId)")
    Optional<ChatRoom> findByIdAndUserId(UUID roomId, UUID userId);
}
