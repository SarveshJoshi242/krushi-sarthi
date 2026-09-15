package com.krushiadhaar.marketplace.controller;

import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam("listingId") UUID listingId,
            @RequestParam("requestedQuantity") BigDecimal requestedQuantity,
            @RequestParam("idempotencyKey") UUID idempotencyKey) {
        return ResponseEntity.ok(orderService.createOrder(getUserId(), listingId, requestedQuantity, idempotencyKey));
    }
}
