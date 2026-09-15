package com.krushiadhaar.integration;

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
import com.krushiadhaar.marketplace.service.OrderService;
import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.common.exception.CannotPurchaseOwnListingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EndToEndRecoveryTest {

    @Autowired private OrderService orderService;
    @Autowired private HarvestInventoryRepository inventoryRepository;
    @Autowired private MarketplaceListingRepository listingRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CropRepository cropRepository;

    private User farmer;
    private User buyer;
    private MarketplaceListing publicListing;

    @BeforeEach
    void setup() {
        farmer = userRepository.save(User.builder().phone("5551").password("p").build());
        buyer = userRepository.save(User.builder().phone("5552").password("p").build());
        Crop crop = cropRepository.save(new Crop(null, "Test Crop", "TEST"));
        
        HarvestInventory inv = HarvestInventory.builder()
            .harvestId(UUID.randomUUID())
            .cropId(crop.getId())
            .quantityAvailable(new BigDecimal("100.000"))
            .quantityReserved(BigDecimal.ZERO)
            .quantitySold(BigDecimal.ZERO)
            .unit("kg")
            .status("AVAILABLE")
            .build();
        inv = inventoryRepository.save(inv);
        
        publicListing = MarketplaceListing.builder()
            .sellerUserId(farmer.getId())
            .inventory(inv)
            .cropId(crop.getId())
            .title("Organic Apples")
            .pricePerUnit(new BigDecimal("15.00"))
            .status("ACTIVE")
            .build();
        publicListing = listingRepository.save(publicListing);
    }

    @Test
    void testBuyerCannotBuyOwnListing() {
        assertThrows(CannotPurchaseOwnListingException.class, () -> {
            orderService.createOrder(farmer.getId(), publicListing.getId(), new BigDecimal("10.000"), UUID.randomUUID());
        });
    }

    @Test
    void testSuccessfulEndToEndOrderAndInventoryDeduction() {
        // Buyer creates order
        Order order = orderService.createOrder(buyer.getId(), publicListing.getId(), new BigDecimal("10.000"), UUID.randomUUID());
        
        assertNotNull(order.getId());
        assertEquals(new BigDecimal("150.00"), order.getTotalAmount());
        
        // Verify inventory was mathematically deducted correctly
        HarvestInventory updatedInv = inventoryRepository.findById(publicListing.getInventory().getId()).orElseThrow();
        assertEquals(new BigDecimal("90.000"), updatedInv.getQuantityAvailable());
        assertEquals(new BigDecimal("10.000"), updatedInv.getQuantityReserved());
    }
}
