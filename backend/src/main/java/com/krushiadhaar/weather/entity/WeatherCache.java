package com.krushiadhaar.weather.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weather_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherCache {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    private BigDecimal temperature;
    private BigDecimal feelsLike;
    private BigDecimal humidity;
    private BigDecimal rainProbability;
    private BigDecimal windSpeed;
    private String weatherCondition;
    private String weatherDescription;
    private String weatherIcon;

    @Column(columnDefinition = "TEXT")
    private String forecastJson;

    private LocalDateTime fetchedAt;
    private LocalDateTime expiresAt;
}
