ALTER TABLE disease_results ADD COLUMN IF NOT EXISTS medicines TEXT;
ALTER TABLE disease_results ADD COLUMN IF NOT EXISTS estimated_cost DECIMAL(10,2);
