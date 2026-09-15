
-- Alter orders to add payment deadline
ALTER TABLE orders ADD COLUMN payment_deadline TIMESTAMP;

-- Create partial index for expiration discovery
CREATE INDEX idx_orders_payment_expiration 
ON orders (payment_deadline) 
WHERE status = 'CONFIRMED';
