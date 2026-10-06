package com.krushiadhaar.weather.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Primary
public class OpenWeatherMapProvider implements WeatherProvider {

    private final RestTemplate restTemplate;
    
    // Hardcoding as requested, though normally this would be in application.properties
    private final String apiKey = "20fe0a561f9832b040c30f5c1aaf8d6c";
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";

    public OpenWeatherMapProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherData getFarmWeather(double latitude, double longitude) {
        String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", baseUrl, latitude, longitude, apiKey);
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return getFallback();

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            List<Map<String, Object>> weather = (List<Map<String, Object>>) response.get("weather");

            WeatherData data = new WeatherData();
            if (main != null) {
                data.setTemperature(getDouble(main.get("temp")));
                data.setFeelsLike(getDouble(main.get("feels_like")));
                data.setHumidity(getDouble(main.get("humidity")));
            }
            if (wind != null) {
                data.setWindSpeed(getDouble(wind.get("speed")));
            }
            if (weather != null && !weather.isEmpty()) {
                data.setCondition((String) weather.get(0).get("main"));
            }
            // OWM current weather doesn't have pop (probability of precipitation), fallback to 0
            data.setRainProbability(0.0);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return getFallback();
        }
    }
    
    private double getDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return 0.0;
    }
    
    private WeatherData getFallback() {
        WeatherData data = new WeatherData();
        data.setTemperature(25.0);
        data.setFeelsLike(26.0);
        data.setHumidity(60.0);
        data.setRainProbability(0.0);
        data.setWindSpeed(10.0);
        data.setCondition("Clear");
        return data;
    }
}
