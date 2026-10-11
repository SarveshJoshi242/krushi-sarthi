package com.krushiadhaar.weather.service;

import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.farm.entity.Farm;
import com.krushiadhaar.farm.repository.FarmRepository;
import com.krushiadhaar.weather.entity.WeatherCache;
import com.krushiadhaar.weather.provider.WeatherProvider;
import com.krushiadhaar.weather.provider.WeatherData;
import com.krushiadhaar.weather.repository.WeatherCacheRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeatherService {
    private final FarmRepository farmRepository;
    private final WeatherProvider weatherProvider;
    private final WeatherCacheRepository weatherCacheRepository;

    // Default coordinates (Delhi, India) as fallback
    private static final double DEFAULT_LAT = 28.6139;
    private static final double DEFAULT_LON = 77.2090;

    public WeatherService(FarmRepository farmRepository, WeatherProvider weatherProvider,
                          WeatherCacheRepository weatherCacheRepository) {
        this.farmRepository = farmRepository;
        this.weatherProvider = weatherProvider;
        this.weatherCacheRepository = weatherCacheRepository;
    }

    public WeatherResponse getWeather(UUID farmId, UUID ownerId) {
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, ownerId)
                .orElseThrow(() -> new UnauthorizedResourceAccessException("Farm not found or access denied"));

        double lat = farm.getLatitude() != null ? farm.getLatitude().doubleValue() : DEFAULT_LAT;
        double lon = farm.getLongitude() != null ? farm.getLongitude().doubleValue() : DEFAULT_LON;

        // Check cache first (30-minute cache)
        Optional<WeatherCache> cached = weatherCacheRepository.findValidCache(
                BigDecimal.valueOf(lat), BigDecimal.valueOf(lon), LocalDateTime.now());

        WeatherData data;
        if (cached.isPresent()) {
            WeatherCache cache = cached.get();
            data = new WeatherData();
            data.setTemperature(cache.getTemperature() != null ? cache.getTemperature().doubleValue() : 0);
            data.setFeelsLike(cache.getFeelsLike() != null ? cache.getFeelsLike().doubleValue() : 0);
            data.setHumidity(cache.getHumidity() != null ? cache.getHumidity().doubleValue() : 0);
            data.setRainProbability(cache.getRainProbability() != null ? cache.getRainProbability().doubleValue() : 0);
            data.setWindSpeed(cache.getWindSpeed() != null ? cache.getWindSpeed().doubleValue() : 0);
            data.setCondition(cache.getWeatherCondition());
            data.setDescription(cache.getWeatherDescription());
            data.setIcon(cache.getWeatherIcon());
        } else {
            data = weatherProvider.getFarmWeather(lat, lon);
            // Save to cache
            saveToCache(lat, lon, data);
        }

        List<String> insights = generateInsights(data);
        return new WeatherResponse(data, insights);
    }

    public WeatherResponse getWeatherByCoordinates(double lat, double lon) {
        WeatherData data = weatherProvider.getFarmWeather(lat, lon);
        List<String> insights = generateInsights(data);
        return new WeatherResponse(data, insights);
    }

    private void saveToCache(double lat, double lon, WeatherData data) {
        try {
            WeatherCache cache = WeatherCache.builder()
                    .latitude(BigDecimal.valueOf(lat))
                    .longitude(BigDecimal.valueOf(lon))
                    .temperature(BigDecimal.valueOf(data.getTemperature()))
                    .feelsLike(BigDecimal.valueOf(data.getFeelsLike()))
                    .humidity(BigDecimal.valueOf(data.getHumidity()))
                    .rainProbability(BigDecimal.valueOf(data.getRainProbability()))
                    .windSpeed(BigDecimal.valueOf(data.getWindSpeed()))
                    .weatherCondition(data.getCondition())
                    .weatherDescription(data.getDescription())
                    .weatherIcon(data.getIcon())
                    .fetchedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .build();
            weatherCacheRepository.save(cache);
        } catch (Exception e) {
            System.err.println("Failed to cache weather data: " + e.getMessage());
        }
    }

    private List<String> generateInsights(WeatherData data) {
        List<String> insights = new ArrayList<>();
        if (data.getRainProbability() > 70.0) {
            insights.add("\uD83C\uDF27\uFE0F High rain probability: Delay irrigation and pesticide spraying.");
        }
        if (data.getTemperature() > 38.0) {
            insights.add("\u2600\uFE0F Heat warning: Ensure adequate water supply for crops. Consider shade nets.");
        }
        if (data.getTemperature() < 5.0) {
            insights.add("\u2744\uFE0F Frost warning: Protect sensitive crops with mulching or covers.");
        }
        if (data.getHumidity() > 80.0) {
            insights.add("\uD83C\uDF3F Disease risk: High humidity may increase fungal disease susceptibility.");
        }
        if (data.getWindSpeed() > 25.0) {
            insights.add("\uD83D\uDCA8 Strong winds: Secure greenhouse structures and support tall crops.");
        }
        if (insights.isEmpty()) {
            insights.add("\u2705 Weather conditions are favorable for farming activities.");
        }
        return insights;
    }
}
