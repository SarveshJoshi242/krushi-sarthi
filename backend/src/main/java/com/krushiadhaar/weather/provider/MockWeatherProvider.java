package com.krushiadhaar.weather.provider;
import org.springframework.stereotype.Component;
@Component
public class MockWeatherProvider implements WeatherProvider {
    @Override
    public WeatherData getFarmWeather(double latitude, double longitude) {
        WeatherData data = new WeatherData();
        data.setTemperature(32.5);
        data.setFeelsLike(34.0);
        data.setHumidity(85.0);
        data.setRainProbability(75.0);
        data.setWindSpeed(15.2);
        data.setCondition("Cloudy");
        return data;
    }
}
