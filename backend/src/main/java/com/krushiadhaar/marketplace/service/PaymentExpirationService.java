package com.krushiadhaar.marketplace.service;
import com.krushiadhaar.common.exception.ResourceNotFoundException;

import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.entity.HarvestInventory;
import com.krushiadhaar.marketplace.entity.OrderItem;
import com.krushiadhaar.marketplace.repository.OrderRepository;
import com.krushiadhaar.marketplace.repository.HarvestInventoryRepository;
import com.krushiadhaar.marketplace.repository.OrderItemRepository;
import com.krushiadhaar.notification.service.NotificationService;
import com.krushiadhaar.notification.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentExpirationService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentExpirationService.class);
    private final OrderRepository orderRepository;
    private final HarvestInventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final NotificationService notificationService;

    public PaymentExpirationService(OrderRepository orderRepository, HarvestInventoryRepository inventoryRepository, OrderItemRepository orderItemRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void expireOrder(UUID orderId) {
        // 1. Lock the order pessimistically
        Order order = orderRepository.findByIdForUpdate(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 2. Multi-instance safety re-check
        if (!"CONFIRMED".equals(order.getStatus()) || order.getPaymentDeadline() == null || order.getPaymentDeadline().isAfter(LocalDateTime.now())) {
            return; // Order was already modified by another instance or webhook
        }

        // 3. Mark as CANCELLED
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        // 4. Release Inventory
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            HarvestInventory inv = inventoryRepository.findByIdForUpdate(item.getListing().getInventory().getId())
                .orElseThrow();
            inv.setQuantityAvailable(inv.getQuantityAvailable().add(item.getQuantity()));
            inv.setQuantityReserved(inv.getQuantityReserved().subtract(item.getQuantity()));
            
            if (inv.getQuantityReserved().compareTo(java.math.BigDecimal.ZERO) == 0 && inv.getQuantitySold().compareTo(java.math.BigDecimal.ZERO) == 0) {
                inv.setStatus("AVAILABLE");
            } else if (inv.getQuantityAvailable().compareTo(java.math.BigDecimal.ZERO) > 0) {
                inv.setStatus("PARTIALLY_RESERVED");
            }
            inventoryRepository.save(inv);
        }

        // 5. Fire Notification (transactional commit trigger)
        Notification notification = Notification.builder()
            .userId(order.getBuyerUserId())
            .type("ORDER_CANCELLED")
            .title("Order Expired")
            .body("Your order has expired due to lack of payment.")
            .resourceType("ORDER")
            .resourceId(order.getId())
            .build();
        notificationService.createAndPublish(notification);
        
        logger.info("Order {} successfully expired and inventory released.", orderId);
    }
}
