package com.krushiadhaar.weather.provider;
public interface WeatherProvider {
    WeatherData getFarmWeather(double latitude, double longitude);
}
