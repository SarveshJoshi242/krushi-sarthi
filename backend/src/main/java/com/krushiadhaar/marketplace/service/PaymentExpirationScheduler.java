package com.krushiadhaar.marketplace.service;

import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PaymentExpirationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PaymentExpirationScheduler.class);
    private final OrderRepository orderRepository;
    private final PaymentExpirationService expirationService;

    public PaymentExpirationScheduler(OrderRepository orderRepository, PaymentExpirationService expirationService) {
        this.orderRepository = orderRepository;
        this.expirationService = expirationService;
    }

    @Scheduled(fixedDelayString = "")
    public void sweepExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> candidates = orderRepository.findExpiredCandidates("CONFIRMED", now);
        
        if (!candidates.isEmpty()) {
            logger.info("Found {} candidate orders for expiration", candidates.size());
        }

        for (Order candidate : candidates) {
            try {
                // Process each order in its own transaction batch
                expirationService.expireOrder(candidate.getId());
            } catch (Exception e) {
                logger.error("Failed to expire order {}", candidate.getId(), e);
            }
        }
    }
}
