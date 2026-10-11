-- Government agricultural schemes
CREATE TABLE IF NOT EXISTS government_schemes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    eligibility TEXT,
    benefits TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    link VARCHAR(500),
    required_documents TEXT,
    scheme_type VARCHAR(50),
    applicable_states TEXT,
    applicable_crops TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_schemes_active ON government_schemes(is_active);
CREATE INDEX idx_schemes_type ON government_schemes(scheme_type);

-- Insert default well-known schemes
INSERT INTO government_schemes (title, provider, description, is_active, link, required_documents, scheme_type) VALUES
('PM-KISAN', 'Central Government', 'Pradhan Mantri Kisan Samman Nidhi - Income support of Rs. 6,000 per year in 3 installments to all land-holding farmers.', TRUE, 'https://pmkisan.gov.in/', 'Aadhaar Card, Bank Account Details, Land Records', 'SUBSIDY'),
('PMFBY', 'Central Government', 'Pradhan Mantri Fasal Bima Yojana - Comprehensive crop insurance scheme to protect farmers against crop failure due to natural calamities, pests and diseases.', TRUE, 'https://pmfby.gov.in/', 'Land Records, Aadhaar Card, Bank Account', 'INSURANCE'),
('Soil Health Card Scheme', 'Central Government', 'Provides soil health cards to farmers with crop-wise nutrient recommendations for improved productivity.', TRUE, 'https://soilhealth.dac.gov.in/', 'Aadhaar Card, Land Records', 'ADVISORY'),
('PM-KUSUM', 'Central Government', 'Pradhan Mantri Kisan Urja Suraksha evam Utthan Mahabhiyan - Solar energy scheme for farmers to set up solar pumps and grid-connected solar power plants.', TRUE, 'https://pmkusum.mnre.gov.in/', 'Aadhaar Card, Land Records, Bank Account', 'SUBSIDY'),
('eNAM', 'Central Government', 'National Agriculture Market - Online trading platform for agricultural commodities providing better price discovery and transparent sale transactions.', TRUE, 'https://enam.gov.in/', 'Aadhaar Card, Bank Account, Trader License', 'MARKET'),
('KCC', 'Central Government', 'Kisan Credit Card - Provides adequate and timely credit support to farmers for their agriculture and other needs at low interest rates.', TRUE, 'https://www.pmkisan.gov.in/KCC.aspx', 'Aadhaar Card, Land Records, Passport Photo', 'CREDIT'),
('RKVY', 'Central Government', 'Rashtriya Krishi Vikas Yojana - Incentivizes states to increase public investment in agriculture and allied sectors.', TRUE, 'https://rkvy.nic.in/', 'State-specific documents', 'SUBSIDY'),
('NMSA', 'Central Government', 'National Mission for Sustainable Agriculture - Promotes sustainable agriculture through climate change adaptation measures.', TRUE, 'https://nmsa.dac.gov.in/', 'Aadhaar Card, Land Records', 'ADVISORY');
