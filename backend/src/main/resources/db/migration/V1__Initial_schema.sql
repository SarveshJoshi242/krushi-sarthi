CREATE TABLE dummy_verification (
    id SERIAL PRIMARY KEY,
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL
);

INSERT INTO dummy_verification (status) VALUES ('FOUNDATION_VERIFIED');
