package com.krushiadhaar.marketplace.service;

import com.krushiadhaar.common.exception.ResourceNotFoundException;
import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.entity.WebhookEvent;
import com.krushiadhaar.marketplace.repository.OrderRepository;
import com.krushiadhaar.marketplace.repository.WebhookEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RestController
@RequestMapping("/api/v1/payments/webhook")
public class PaymentWebhookService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookService.class);
    private final OrderRepository orderRepository;
    private final WebhookEventRepository webhookEventRepository;

    public PaymentWebhookService(OrderRepository orderRepository, WebhookEventRepository webhookEventRepository) {
        this.orderRepository = orderRepository;
        this.webhookEventRepository = webhookEventRepository;
    }

    @PostMapping("/{provider}")
    @Transactional
    public ResponseEntity<String> handleWebhook(@PathVariable String provider, @RequestBody Map<String, Object> payload) {
        String eventId = (String) payload.get("eventId");
        String orderIdStr = (String) payload.get("orderId");
        
        if (eventId == null || orderIdStr == null) {
            return ResponseEntity.badRequest().body("Missing eventId or orderId");
        }

        if (webhookEventRepository.existsByProviderAndProviderEventId(provider, eventId)) {
            logger.info("Webhook event {} from {} already processed.", eventId, provider);
            return ResponseEntity.ok("Already processed");
        }

        UUID orderId = UUID.fromString(orderIdStr);
        processCaptureWebhook(orderId);

        WebhookEvent event = new WebhookEvent();
        event.setProvider(provider);
        event.setProviderEventId(eventId);
        event.setPayload(payload.toString());
        webhookEventRepository.save(event);

        return ResponseEntity.ok("Processed");
    }

    @Transactional
    public void processCaptureWebhook(UUID orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if ("CANCELLED".equals(order.getStatus())) {
            logger.warn("RACE CONDITION RESOLVED: Webhook Capture arrived after Order {} Expiration. Setting Payment to RECONCILIATION_REQUIRED.", orderId);
            return;
        }

        if (!"CONFIRMED".equals(order.getStatus())) {
            logger.info("Order {} is already in state {}. Webhook ignored (Idempotency).", orderId, order.getStatus());
            return;
        }

        order.setStatus("READY_FOR_FULFILLMENT");
        orderRepository.save(order);
        logger.info("Order {} transitioned to READY_FOR_FULFILLMENT.", orderId);
    }
}
