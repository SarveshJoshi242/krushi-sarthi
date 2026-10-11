-- Weather data cache to avoid excessive API calls
CREATE TABLE IF NOT EXISTS weather_cache (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    temperature DECIMAL(5,2),
    feels_like DECIMAL(5,2),
    humidity DECIMAL(5,2),
    rain_probability DECIMAL(5,2),
    wind_speed DECIMAL(5,2),
    weather_condition VARCHAR(50),
    weather_description VARCHAR(200),
    weather_icon VARCHAR(10),
    forecast_json TEXT,
    fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_weather_cache_location ON weather_cache(latitude, longitude);
CREATE INDEX idx_weather_cache_expiry ON weather_cache(expires_at);
