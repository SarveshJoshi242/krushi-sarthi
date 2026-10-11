-- V17: Create Mandi Prices and Soil Health Records tables
CREATE TABLE IF NOT EXISTS mandi_prices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    commodity VARCHAR(100) NOT NULL,
    mandi_name VARCHAR(150) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    modal_price NUMERIC(10,2) NOT NULL,
    min_price NUMERIC(10,2),
    max_price NUMERIC(10,2),
    msp NUMERIC(10,2),
    price_unit VARCHAR(50) DEFAULT 'Quintal',
    trend_percentage VARCHAR(20),
    trend_direction VARCHAR(10) DEFAULT 'UP',
    recorded_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS soil_health_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    field_name VARCHAR(100) NOT NULL,
    crop VARCHAR(100) NOT NULL,
    acreage NUMERIC(5,2) DEFAULT 1.0,
    nitrogen_kg_ha NUMERIC(7,2) NOT NULL,
    phosphorus_kg_ha NUMERIC(7,2) NOT NULL,
    potassium_kg_ha NUMERIC(7,2) NOT NULL,
    ph_value NUMERIC(4,2) NOT NULL,
    urea_bags_prescribed NUMERIC(5,2),
    dap_bags_prescribed NUMERIC(5,2),
    mop_bags_prescribed NUMERIC(5,2),
    amendment_advice TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_mandi_commodity ON mandi_prices(commodity);
CREATE INDEX idx_soil_user ON soil_health_records(user_id);
