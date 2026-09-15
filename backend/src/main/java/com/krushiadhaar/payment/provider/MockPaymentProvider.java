package com.krushiadhaar.payment.provider;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;
@Component
public class MockPaymentProvider implements PaymentProvider {
    public String createPaymentIntent(BigDecimal amount, String currency, String orderId) {
        return "txn_" + UUID.randomUUID().toString();
    }
    public boolean verifyPayment(String transactionId) {
        return true;
    }
}
