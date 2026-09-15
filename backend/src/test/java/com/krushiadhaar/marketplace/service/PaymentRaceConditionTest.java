package com.krushiadhaar.marketplace.service;

import com.krushiadhaar.crop.entity.Crop;
import com.krushiadhaar.crop.repository.CropRepository;
import com.krushiadhaar.marketplace.entity.HarvestInventory;
import com.krushiadhaar.marketplace.entity.MarketplaceListing;
import com.krushiadhaar.marketplace.entity.Order;
import com.krushiadhaar.marketplace.entity.OrderItem;
import com.krushiadhaar.marketplace.repository.HarvestInventoryRepository;
import com.krushiadhaar.marketplace.repository.MarketplaceListingRepository;
import com.krushiadhaar.marketplace.repository.OrderRepository;
import com.krushiadhaar.marketplace.repository.OrderItemRepository;
import com.krushiadhaar.user.entity.User;
import com.krushiadhaar.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentRaceConditionTest {

    @Autowired private PaymentExpirationService expirationService;
    @Autowired private PaymentWebhookService webhookService;
    @Autowired private HarvestInventoryRepository inventoryRepository;
    @Autowired private MarketplaceListingRepository listingRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CropRepository cropRepository;

    private UUID orderId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(User.builder().phone("555").password("p").build());
        Crop crop = cropRepository.save(new Crop(null, "Test Crop", "TEST"));
        
        HarvestInventory inv = HarvestInventory.builder()
            .harvestId(UUID.randomUUID())
            .cropId(crop.getId())
            .quantityAvailable(new BigDecimal("0.000"))
            .quantityReserved(new BigDecimal("10.000"))
            .quantitySold(BigDecimal.ZERO)
            .unit("kg")
            .build();
        inv = inventoryRepository.save(inv);
        
        MarketplaceListing listing = MarketplaceListing.builder()
            .sellerUserId(user.getId())
            .inventory(inv)
            .cropId(crop.getId())
            .title("Test")
            .pricePerUnit(new BigDecimal("10.00"))
            .status("ACTIVE")
            .build();
        listing = listingRepository.save(listing);

        Order order = Order.builder()
            .buyerUserId(user.getId())
            .status("CONFIRMED")
            .subtotal(new BigDecimal("100.00"))
            .totalAmount(new BigDecimal("100.00"))
            .idempotencyKey(UUID.randomUUID())
            .paymentDeadline(LocalDateTime.now().minusMinutes(1)) // Expired
            .build();
        order = orderRepository.save(order);
        this.orderId = order.getId();

        OrderItem item = OrderItem.builder()
            .order(order)
            .listing(listing)
            .quantity(new BigDecimal("10.000"))
            .unitPrice(new BigDecimal("10.00"))
            .subtotal(new BigDecimal("100.00"))
            .build();
        orderItemRepository.save(item);
    }

    @Test
    void testCaptureVsExpirationRace() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        Runnable expirationTask = () -> {
            try {
                latch.await();
                expirationService.expireOrder(orderId);
            } catch (Exception e) {} finally {
                doneLatch.countDown();
            }
        };

        Runnable webhookTask = () -> {
            try {
                latch.await();
                webhookService.processCaptureWebhook(orderId);
            } catch (Exception e) {} finally {
                doneLatch.countDown();
            }
        };

        executorService.submit(expirationTask);
        executorService.submit(webhookTask);

        latch.countDown(); // Release both simultaneously
        doneLatch.await(); // Wait for both to finish

        Order finalOrder = orderRepository.findById(orderId).orElseThrow();
        String status = finalOrder.getStatus();
        
        // Assert exactly one outcome occurs safely and definitively
        assertTrue(status.equals("CANCELLED") || status.equals("READY_FOR_FULFILLMENT"),
            "Status must deterministically resolve to CANCELLED or READY_FOR_FULFILLMENT, but was " + status);
    }
}
