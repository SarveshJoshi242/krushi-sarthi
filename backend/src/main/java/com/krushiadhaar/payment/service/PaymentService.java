package com.krushiadhaar.payment.service;

import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.repository.OrderRepository;
import com.krushiadhaar.payment.entity.Payment;
import com.krushiadhaar.payment.repository.PaymentRepository;
import com.krushiadhaar.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Payment createPaymentAttempt(UUID orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (paymentRepository.findByOrderIdAndStatus(orderId, "CAPTURED").isPresent()) {
            throw new IllegalStateException("Order already has a captured payment");
        }

        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setStatus("PENDING");
        payment.setPaymentMethod(paymentMethod);

        return paymentRepository.save(payment);
    }

    public Payment capturePayment(UUID paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if ("CAPTURED".equals(payment.getStatus())) {
            throw new IllegalStateException("Payment already captured");
        }

        if (paymentRepository.findByOrderIdAndStatus(payment.getOrder().getId(), "CAPTURED").isPresent()) {
            throw new IllegalStateException("Order already has a captured payment");
        }

        payment.setStatus("CAPTURED");
        payment.setTransactionId(transactionId);
        
        Order order = payment.getOrder();
        order.setStatus("PAID");
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }
}
