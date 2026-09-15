package com.krushiadhaar.payment.service;
import com.krushiadhaar.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
@Service
public class PaymentReconciliationService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentReconciliationService.class);
    private final PaymentRepository paymentRepository;
    public PaymentReconciliationService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    @Scheduled(fixedRate = 60000)
    public void reconcile() {
        logger.info("Running reconciliation");
    }
}
