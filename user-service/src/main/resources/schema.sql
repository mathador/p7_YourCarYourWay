-- Full schema based on entity diagram

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    username VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    password_hash VARCHAR(255),
    role VARCHAR(50) DEFAULT 'CLIENT',
    active BOOLEAN DEFAULT TRUE,
    is_psh_profile BOOLEAN DEFAULT FALSE
);

-- Agencies table
CREATE TABLE IF NOT EXISTS agencies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    timezone VARCHAR(50)
);

-- Vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id SERIAL PRIMARY KEY,
    acriss_code VARCHAR(20),
    brand VARCHAR(100),
    model VARCHAR(100),
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    price_per_day FLOAT,
    agency_id INTEGER REFERENCES agencies(id)
);

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    amount FLOAT,
    payment_id VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    user_id INTEGER REFERENCES users(id),
    vehicle_id INTEGER REFERENCES vehicles(id)
);

-- Chat Sessions table
CREATE TABLE IF NOT EXISTS chat_sessions (
    session_id UUID PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    guest_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    country_code VARCHAR(10),
    status VARCHAR(50) DEFAULT 'OPEN'
);

-- Chat Messages table
CREATE TABLE IF NOT EXISTS chat_messages (
    id SERIAL PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES chat_sessions(session_id),
    sender_id INTEGER REFERENCES users(id),
    sender_username VARCHAR(255),
    content TEXT NOT NULL,
    timestamp_utc TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    language_code VARCHAR(10)
);

-- Index for chat messages
CREATE INDEX IF NOT EXISTS idx_messages_session_ts ON chat_messages(session_id, timestamp_utc);
