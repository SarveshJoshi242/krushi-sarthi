package com.krushiadhaar.weather.service;
import com.krushiadhaar.weather.provider.WeatherData;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data
@AllArgsConstructor
public class WeatherResponse {
    private WeatherData weather;
    private List<String> insights;
}
