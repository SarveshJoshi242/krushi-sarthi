package com.krushiadhaar.marketplace.controller;

import com.krushiadhaar.common.response.ApiResponse;
import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final OrderService orderService;

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/listings")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getListings() {
        // Placeholder data
        return ResponseEntity.ok(ApiResponse.success(Collections.emptyList()));
    }

    @PostMapping("/listings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createListing(@RequestBody Map<String, Object> request) {
        // Placeholder data
        return ResponseEntity.ok(ApiResponse.success(request));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @RequestParam("listingId") UUID listingId,
            @RequestParam("requestedQuantity") BigDecimal requestedQuantity,
            @RequestParam("idempotencyKey") UUID idempotencyKey) {
        Order order = orderService.createOrder(getUserId(), listingId, requestedQuantity, idempotencyKey);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
