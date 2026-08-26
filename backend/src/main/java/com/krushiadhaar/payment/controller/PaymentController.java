package com.krushiadhaar.payment.controller;

import com.krushiadhaar.payment.entity.Payment;
import com.krushiadhaar.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<Payment> createPaymentAttempt(@PathVariable UUID orderId, @RequestParam String paymentMethod) {
        // Authenticate via SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(paymentService.createPaymentAttempt(orderId, paymentMethod));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<Payment> capturePayment(@PathVariable UUID paymentId, @RequestParam String transactionId) {
        return ResponseEntity.ok(paymentService.capturePayment(paymentId, transactionId));
    }
}
