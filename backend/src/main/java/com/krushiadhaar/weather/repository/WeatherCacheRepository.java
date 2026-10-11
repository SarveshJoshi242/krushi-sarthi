package com.krushiadhaar.weather.repository;

import com.krushiadhaar.weather.entity.WeatherCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface WeatherCacheRepository extends JpaRepository<WeatherCache, UUID> {
    @Query("SELECT w FROM WeatherCache w WHERE w.latitude = :lat AND w.longitude = :lon AND w.expiresAt > :now ORDER BY w.fetchedAt DESC")
    Optional<WeatherCache> findValidCache(BigDecimal lat, BigDecimal lon, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
