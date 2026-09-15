package com.krushiadhaar;

import com.krushiadhaar.farm.entity.Farm;
import com.krushiadhaar.farm.service.FarmService;
import com.krushiadhaar.farm.entity.Field;
import com.krushiadhaar.farm.service.FieldService;
import com.krushiadhaar.crop.entity.Crop;
import com.krushiadhaar.crop.repository.CropRepository;
import com.krushiadhaar.disease.entity.DiseaseScan;
import com.krushiadhaar.disease.service.DiseaseService;
import com.krushiadhaar.weather.service.WeatherService;
import com.krushiadhaar.weather.service.WeatherResponse;
import com.krushiadhaar.marketplace.service.PaymentWebhookService;
import com.krushiadhaar.marketplace.entity.WebhookEvent;
import com.krushiadhaar.marketplace.repository.WebhookEventRepository;
import com.krushiadhaar.user.entity.User;
import com.krushiadhaar.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class E2ETest {

    @Autowired private FarmService farmService;
    @Autowired private FieldService fieldService;
    @Autowired private DiseaseService diseaseService;
    @Autowired private WeatherService weatherService;
    @Autowired private PaymentWebhookService webhookService;
    @Autowired private UserRepository userRepository;
    @Autowired private CropRepository cropRepository;
    @Autowired private WebhookEventRepository webhookEventRepository;

    private User farmer;
    private User buyer;

    @BeforeEach
    void setup() {
        farmer = userRepository.save(User.builder().phone("555-FARMER").password("p").build());
        buyer = userRepository.save(User.builder().phone("555-BUYER").password("p").build());
    }

    @Test
    void testFarmerFlowAndMultiUserIsolation() {
        // Farmer creates a farm
        Farm f = new Farm();
        f.setName("My Farm");
        f.setTotalArea(new java.math.BigDecimal("10.0"));
        Farm farm = farmService.createFarm(farmer.getId(), f);
        assertNotNull(farm.getId());

        // Multi-user isolation: Buyer cannot see farmer's farm
        assertThrows(Exception.class, () -> {
            farmService.getFarm(farm.getId(), buyer.getId());
        });

        // Weather
        WeatherResponse w = weatherService.getWeather(farm.getId(), farmer.getId());
        assertNotNull(w);
        assertNotNull(w.getTemperature());
    }

    @Test
    void testWebhookIdempotency() {
        String provider = "stripe";
        String eventId = "evt_12345";
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", eventId);
        payload.put("orderId", UUID.randomUUID().toString());

        ResponseEntity<String> res1 = webhookService.handleWebhook(provider, payload);
        assertEquals("Processed", res1.getBody());

        ResponseEntity<String> res2 = webhookService.handleWebhook(provider, payload);
        assertEquals("Already processed", res2.getBody());
    }
}
