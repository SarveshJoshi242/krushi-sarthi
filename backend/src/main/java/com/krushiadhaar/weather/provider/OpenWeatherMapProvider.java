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

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.base-url:https://api.openweathermap.org/data/2.5}")
    private String baseUrl;

    public OpenWeatherMapProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherData getFarmWeather(double latitude, double longitude) {
        String url = String.format("%s/weather?lat=%s&lon=%s&appid=%s&units=metric",
                baseUrl, latitude, longitude, apiKey);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return getFallback();

            @SuppressWarnings("unchecked")
            Map<String, Object> main = (Map<String, Object>) response.get("main");
            @SuppressWarnings("unchecked")
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            @SuppressWarnings("unchecked")
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
                data.setDescription((String) weather.get(0).get("description"));
                data.setIcon((String) weather.get(0).get("icon"));
            }
            data.setRainProbability(0.0);

            return data;
        } catch (Exception e) {
            System.err.println("OpenWeatherMap API error: " + e.getMessage());
            return getFallback();
        }
    }

    public WeatherData getForecast(double latitude, double longitude) {
        String url = String.format("%s/forecast?lat=%s&lon=%s&appid=%s&units=metric&cnt=8",
                baseUrl, latitude, longitude, apiKey);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return getFallback();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> forecastList = (List<Map<String, Object>>) response.get("list");
            if (forecastList != null && !forecastList.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> first = forecastList.get(0);
                @SuppressWarnings("unchecked")
                Map<String, Object> main = (Map<String, Object>) first.get("main");

                WeatherData data = new WeatherData();
                if (main != null) {
                    data.setTemperature(getDouble(main.get("temp")));
                    data.setFeelsLike(getDouble(main.get("feels_like")));
                    data.setHumidity(getDouble(main.get("humidity")));
                }
                // Check for precipitation probability
                if (first.containsKey("pop")) {
                    data.setRainProbability(getDouble(first.get("pop")) * 100);
                }
                return data;
            }
        } catch (Exception e) {
            System.err.println("OpenWeatherMap Forecast API error: " + e.getMessage());
        }
        return getFallback();
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
        data.setDescription("clear sky");
        data.setIcon("01d");
        return data;
    }
}
