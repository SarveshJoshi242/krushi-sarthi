package com.krushiadhaar.weather.provider;
import lombok.Data;
@Data
public class WeatherData {
    private double temperature;
    private double feelsLike;
    private double humidity;
    private double rainProbability;
    private double windSpeed;
    private String condition;
}
