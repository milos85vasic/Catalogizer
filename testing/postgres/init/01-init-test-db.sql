-- Initialize test database for catalogizer
-- This script runs when the test-postgres container starts

-- Create test schemas
CREATE SCHEMA IF NOT EXISTS catalogizer_test;
CREATE SCHEMA IF NOT EXISTS performance_test;

-- Create test users with specific permissions
CREATE USER IF NOT EXISTS api_test_user WITH PASSWORD 'api_test_password';
CREATE USER IF NOT EXISTS load_test_user WITH PASSWORD 'load_test_password';

-- Grant permissions
GRANT USAGE ON SCHEMA catalogizer_test TO api_test_user;
GRANT CREATE ON SCHEMA catalogizer_test TO api_test_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA catalogizer_test TO api_test_user;

GRANT USAGE ON SCHEMA performance_test TO load_test_user;
GRANT CREATE ON SCHEMA performance_test TO load_test_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA performance_test TO load_test_user;

-- Create test data tables
CREATE TABLE IF NOT EXISTS catalogizer_test.test_media_items (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catalogizer_test.test_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create performance testing tables
CREATE TABLE IF NOT EXISTS performance_test.load_test_results (
    id SERIAL PRIMARY KEY,
    test_name VARCHAR(100) NOT NULL,
    endpoint VARCHAR(200) NOT NULL,
    response_time INTEGER NOT NULL,
    status_code INTEGER NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample test data
INSERT INTO catalogizer_test.test_media_items (title, file_path, file_size) VALUES
('Test Movie 1', '/test/movies/movie1.mp4', 1073741824),
('Test Movie 2', '/test/movies/movie2.mp4', 2147483648),
('Test Song 1', '/test/music/song1.mp3', 10485760),
('Test Song 2', '/test/music/song2.mp3', 8388608);

INSERT INTO catalogizer_test.test_users (username, email) VALUES
('testuser1', 'testuser1@example.com'),
('testuser2', 'testuser2@example.com');

-- Create indexes for performance testing
CREATE INDEX IF NOT EXISTS idx_test_media_items_title ON catalogizer_test.test_media_items(title);
CREATE INDEX IF NOT EXISTS idx_test_media_items_created_at ON catalogizer_test.test_media_items(created_at);
CREATE INDEX IF NOT EXISTS idx_load_test_results_timestamp ON performance_test.load_test_results(timestamp);
CREATE INDEX IF NOT EXISTS idx_load_test_results_test_name ON performance_test.load_test_results(test_name);

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'Test database initialized successfully';
    RAISE NOTICE 'Schema: catalogizer_test, performance_test';
    RAISE NOTICE 'Test users: api_test_user, load_test_user';
END $$;