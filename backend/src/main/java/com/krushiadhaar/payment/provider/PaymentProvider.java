package com.krushiadhaar.payment.provider;
import java.math.BigDecimal;
public interface PaymentProvider {
    String createPaymentIntent(BigDecimal amount, String currency, String orderId);
    boolean verifyPayment(String transactionId);
}
