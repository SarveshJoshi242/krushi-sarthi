package com.krushiadhaar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@ConditionalOnProperty(name = "keepalive.enabled", havingValue = "true", matchIfMissing = true)
public class KeepAliveService {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveService.class);

    private final HttpClient httpClient;

    @Value("${keepalive.self-url:${SELF_URL:${RENDER_EXTERNAL_URL:https://krushi-backend-y60p.onrender.com}}}")
    private String selfUrl;

    @Value("${keepalive.ml-url:${ML_SERVICE_PUBLIC_URL:https://krushi-ml-service-6xfo.onrender.com}}")
    private String mlServicePublicUrl;

    public KeepAliveService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Executes keep-alive ping every 9 minutes (540,000 ms) to keep Render Free Tier services awake.
     * Render spins down services after 15 minutes of inactivity. Pinging every 9 minutes prevents sleep.
     */
    @Scheduled(fixedRate = 540000, initialDelay = 60000)
    public void executeKeepAlive() {
        pingEndpoint(selfUrl, "/api/health/ping", "Krushi-Backend (Self)");
        pingEndpoint(mlServicePublicUrl, "/ping", "Krushi-ML-Service");
    }

    private void pingEndpoint(String baseUrl, String path, String serviceName) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return;
        }
        try {
            String cleanBase = baseUrl.trim().replaceAll("/+$", "");
            if (!cleanBase.startsWith("http://") && !cleanBase.startsWith("https://")) {
                cleanBase = "https://" + cleanBase;
            }
            URI targetUri = URI.create(cleanBase + path);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent", "Krushi-KeepAlive-Backend/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Keep-alive ping to {} [{}]: Status {}", serviceName, targetUri, response.statusCode());
        } catch (Exception e) {
            log.warn("Keep-alive ping to {} failed (non-critical): {}", serviceName, e.getMessage());
        }
    }
}
