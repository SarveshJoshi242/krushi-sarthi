-- Add geolocation columns to farms table
ALTER TABLE farms ADD COLUMN IF NOT EXISTS latitude DECIMAL(10,7);
ALTER TABLE farms ADD COLUMN IF NOT EXISTS longitude DECIMAL(10,7);
ALTER TABLE farms ADD COLUMN IF NOT EXISTS address TEXT;
ALTER TABLE farms ADD COLUMN IF NOT EXISTS city VARCHAR(100);
ALTER TABLE farms ADD COLUMN IF NOT EXISTS state VARCHAR(100);
ALTER TABLE farms ADD COLUMN IF NOT EXISTS pincode VARCHAR(10);

-- Add location to marketplace_listings for distance calculations
ALTER TABLE marketplace_listings ADD COLUMN IF NOT EXISTS farm_latitude DECIMAL(10,7);
ALTER TABLE marketplace_listings ADD COLUMN IF NOT EXISTS farm_longitude DECIMAL(10,7);
ALTER TABLE marketplace_listings ADD COLUMN IF NOT EXISTS location_name VARCHAR(200);
