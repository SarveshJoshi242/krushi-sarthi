package com.krushiadhaar.weather.service;
import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.farm.entity.Farm;
import com.krushiadhaar.farm.repository.FarmRepository;
import com.krushiadhaar.weather.provider.WeatherProvider;
import com.krushiadhaar.weather.provider.WeatherData;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Service
public class WeatherService {
    private final FarmRepository farmRepository;
    private final WeatherProvider weatherProvider;

    public WeatherService(FarmRepository farmRepository, WeatherProvider weatherProvider) {
        this.farmRepository = farmRepository;
        this.weatherProvider = weatherProvider;
    }

    public WeatherResponse getWeather(UUID farmId, UUID ownerId) {
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, ownerId)
            .orElseThrow(() -> new UnauthorizedResourceAccessException("Farm not found or access denied"));
        
        // Cache could be injected here. For Phase 4, using direct provider call if no Cache abstraction is implemented via @Cacheable.
        WeatherData data = weatherProvider.getFarmWeather(
            farm.getTotalArea() != null ? farm.getTotalArea().doubleValue() : 0.0, 
            0.0
        );

        List<String> insights = new ArrayList<>();
        if (data.getRainProbability() > 70.0) {
            insights.add("High rain probability: Delay irrigation.");
        }
        if (data.getTemperature() > 38.0) {
            insights.add("Heat warning: High temperatures expected.");
        }
        if (data.getHumidity() > 80.0) {
            insights.add("Disease risk: High humidity may increase susceptibility.");
        }

        return new WeatherResponse(data, insights);
    }
}
