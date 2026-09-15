
-- Update harvests quantity to higher precision if necessary
ALTER TABLE harvests ALTER COLUMN quantity TYPE NUMERIC(15,3);

CREATE TABLE harvest_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    harvest_id UUID NOT NULL REFERENCES harvests(id) ON DELETE CASCADE UNIQUE,
    crop_id UUID NOT NULL REFERENCES crops(id),
    quantity_available NUMERIC(15,3) NOT NULL DEFAULT 0.000,
    quantity_reserved NUMERIC(15,3) NOT NULL DEFAULT 0.000,
    quantity_sold NUMERIC(15,3) NOT NULL DEFAULT 0.000,
    unit VARCHAR(20) NOT NULL,
    quality_grade VARCHAR(50),
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_qty_available CHECK (quantity_available >= 0),
    CONSTRAINT check_qty_reserved CHECK (quantity_reserved >= 0),
    CONSTRAINT check_qty_sold CHECK (quantity_sold >= 0)
);

CREATE TABLE marketplace_listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    seller_user_id UUID NOT NULL REFERENCES users(id),
    inventory_id UUID NOT NULL REFERENCES harvest_inventory(id) ON DELETE CASCADE,
    crop_id UUID NOT NULL REFERENCES crops(id),
    title VARCHAR(150) NOT NULL,
    description TEXT,
    price_per_unit NUMERIC(15,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'DRAFT',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_price CHECK (price_per_unit > 0)
);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    buyer_user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'PENDING',
    subtotal NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'INR',
    idempotency_key UUID NOT NULL UNIQUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    listing_id UUID NOT NULL REFERENCES marketplace_listings(id),
    quantity NUMERIC(15,3) NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL,
    subtotal NUMERIC(15,2) NOT NULL,
    CONSTRAINT check_order_qty CHECK (quantity > 0)
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id UUID NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_harvest ON harvest_inventory(harvest_id);
CREATE INDEX idx_listings_inventory ON marketplace_listings(inventory_id);
CREATE INDEX idx_listings_seller ON marketplace_listings(seller_user_id);
CREATE INDEX idx_orders_buyer ON orders(buyer_user_id);
CREATE INDEX idx_orders_idemp ON orders(idempotency_key);
CREATE INDEX idx_audit_resource ON audit_logs(resource_type, resource_id);
