package com.krushiadhaar.marketplace.service;

import com.krushiadhaar.crop.entity.Crop;
import com.krushiadhaar.crop.repository.CropRepository;
import com.krushiadhaar.marketplace.entity.HarvestInventory;
import com.krushiadhaar.marketplace.entity.MarketplaceListing;
import com.krushiadhaar.marketplace.repository.HarvestInventoryRepository;
import com.krushiadhaar.marketplace.repository.MarketplaceListingRepository;
import com.krushiadhaar.user.entity.User;
import com.krushiadhaar.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceConcurrencyTest {

    @Autowired private OrderService orderService;
    @Autowired private HarvestInventoryRepository inventoryRepository;
    @Autowired private MarketplaceListingRepository listingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CropRepository cropRepository;

    private UUID buyer1Id;
    private UUID buyer2Id;
    private UUID listingId;
    private UUID inventoryId;

    @BeforeEach
    void setup() {
        User seller = userRepository.save(User.builder().phone("111").password("p").build());
        User buyer1 = userRepository.save(User.builder().phone("222").password("p").build());
        User buyer2 = userRepository.save(User.builder().phone("333").password("p").build());
        
        Crop crop = cropRepository.save(new Crop(null, "Test Crop", "TEST"));
        
        HarvestInventory inv = HarvestInventory.builder()
            .harvestId(UUID.randomUUID())
            .cropId(crop.getId())
            .quantityAvailable(new BigDecimal("10.000"))
            .quantityReserved(BigDecimal.ZERO)
            .quantitySold(BigDecimal.ZERO)
            .unit("kg")
            .build();
        inv = inventoryRepository.save(inv);
        
        MarketplaceListing listing = MarketplaceListing.builder()
            .sellerUserId(seller.getId())
            .inventory(inv)
            .cropId(crop.getId())
            .title("Fresh Crop")
            .pricePerUnit(new BigDecimal("50.00"))
            .status("ACTIVE")
            .build();
        listing = listingRepository.save(listing);

        this.buyer1Id = buyer1.getId();
        this.buyer2Id = buyer2.getId();
        this.listingId = listing.getId();
        this.inventoryId = inv.getId();
    }

    @Test
    void testConcurrentInventoryReservation() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Runnable task = (buyerId) -> {
            try {
                latch.await();
                orderService.createOrder((UUID)buyerId, listingId, new BigDecimal("8.000"), UUID.randomUUID());
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };

        executorService.submit(() -> task.run(buyer1Id));
        executorService.submit(() -> task.run(buyer2Id));

        latch.countDown(); // start threads simultaneously
        doneLatch.await(); // wait for finish

        assertEquals(1, successCount.get(), "Only one order should succeed");
        assertEquals(1, failCount.get(), "One order should fail due to insufficient inventory / locking");

        HarvestInventory finalInv = inventoryRepository.findById(inventoryId).orElseThrow();
        assertEquals(new BigDecimal("2.000"), finalInv.getQuantityAvailable(), "2kg should remain available");
        assertEquals(new BigDecimal("8.000"), finalInv.getQuantityReserved(), "8kg should be reserved");
    }
}
