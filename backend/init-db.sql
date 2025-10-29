-- Create tables first
CREATE TABLE IF NOT EXISTS short_urls (
    short_url VARCHAR(255) PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    user_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    expiration_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS click_events (
    id BIGSERIAL PRIMARY KEY,
    short_url VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    user_agent VARCHAR(1000) NOT NULL
);