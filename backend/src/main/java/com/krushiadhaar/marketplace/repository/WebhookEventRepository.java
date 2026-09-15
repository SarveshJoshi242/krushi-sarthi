package com.krushiadhaar.marketplace.repository;
import com.krushiadhaar.marketplace.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    boolean existsByProviderAndProviderEventId(String provider, String providerEventId);
}
