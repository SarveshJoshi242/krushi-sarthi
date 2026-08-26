package com.krushiadhaar.marketplace.service;
import com.krushiadhaar.common.exception.ResourceNotFoundException;
import com.krushiadhaar.common.exception.InsufficientInventoryException;
import com.krushiadhaar.common.exception.CannotPurchaseOwnListingException;
import com.krushiadhaar.marketplace.entity.*;
import com.krushiadhaar.marketplace.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MarketplaceListingRepository listingRepository;
    private final HarvestInventoryRepository inventoryRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        MarketplaceListingRepository listingRepository, HarvestInventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.listingRepository = listingRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public Order createOrder(UUID buyerId, UUID listingId, BigDecimal requestedQuantity, UUID idempotencyKey) {
        // 1. Idempotency Check
        Optional<Order> existingOrder = orderRepository.findByIdempotencyKeyAndBuyerUserId(idempotencyKey, buyerId);
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        // 2. Load Listing and verify it is ACTIVE
        MarketplaceListing listing = listingRepository.findByIdAndStatus(listingId, "ACTIVE")
            .orElseThrow(() -> new ResourceNotFoundException("Listing not found or not active"));

        if (listing.getSellerUserId().equals(buyerId)) {
            throw new CannotPurchaseOwnListingException("Cannot purchase your own listing");
        }

        // 3. Pessimistic Write Lock on Inventory
        HarvestInventory inventory = inventoryRepository.findByIdForUpdate(listing.getInventory().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        // 4. Verify Available Quantity
        if (inventory.getQuantityAvailable().compareTo(requestedQuantity) < 0) {
            throw new InsufficientInventoryException("Insufficient inventory available");
        }

        // 5. Reserve Quantity (mathematically consistent)
        inventory.setQuantityAvailable(inventory.getQuantityAvailable().subtract(requestedQuantity));
        inventory.setQuantityReserved(inventory.getQuantityReserved().add(requestedQuantity));
        
        // Update status if depleted
        if (inventory.getQuantityAvailable().compareTo(BigDecimal.ZERO) == 0) {
            inventory.setStatus("RESERVED");
        } else {
            inventory.setStatus("PARTIALLY_RESERVED");
        }
        inventoryRepository.save(inventory);

        // 6. Create Order
        BigDecimal subtotal = requestedQuantity.multiply(listing.getPricePerUnit());
        
        Order order = Order.builder()
            .buyerUserId(buyerId)
            .status("CONFIRMED") // Initial state per new constraints
            .subtotal(subtotal)
            .totalAmount(subtotal)
            .idempotencyKey(idempotencyKey)
            .build();
        
        Order savedOrder = orderRepository.save(order);

        // 7. Create Order Item (Snapshot Price)
        OrderItem item = OrderItem.builder()
            .order(savedOrder)
            .listing(listing)
            .quantity(requestedQuantity)
            .unitPrice(listing.getPricePerUnit())
            .subtotal(subtotal)
            .build();
            
        orderItemRepository.save(item);

        return savedOrder;
    }
}
