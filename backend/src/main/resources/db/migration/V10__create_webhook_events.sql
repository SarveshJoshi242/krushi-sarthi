CREATE TABLE webhook_events (
    id UUID PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    provider_event_id VARCHAR(255) NOT NULL,
    payload TEXT,
    processed_at TIMESTAMP NOT NULL,
    UNIQUE(provider, provider_event_id)
);
