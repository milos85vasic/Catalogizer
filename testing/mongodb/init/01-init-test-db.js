// Initialize test MongoDB database for catalogizer
// This script runs when the mongodb container starts

// Switch to test database
db = db.getSiblingDB('catalogizer_test');

// Create test collections
db.createCollection('test_media_items');
db.createCollection('test_users');
db.createCollection('test_sessions');
db.createCollection('performance_metrics');
db.createCollection('test_logs');

// Insert sample test data into test_media_items
db.test_media_items.insertMany([
    {
        title: "Test Movie 1",
        filePath: "/test/movies/movie1.mp4",
        fileSize: 1073741824,
        mediaType: "video",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        title: "Test Movie 2", 
        filePath: "/test/movies/movie2.mp4",
        fileSize: 2147483648,
        mediaType: "video",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        title: "Test Song 1",
        filePath: "/test/music/song1.mp3", 
        fileSize: 10485760,
        mediaType: "audio",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        title: "Test Song 2",
        filePath: "/test/music/song2.mp3",
        fileSize: 8388608,
        mediaType: "audio", 
        createdAt: new Date(),
        updatedAt: new Date()
    }
]);

// Insert sample test data into test_users
db.test_users.insertMany([
    {
        username: "testuser1",
        email: "testuser1@example.com",
        passwordHash: "$2b$10$testhash123",
        role: "user",
        createdAt: new Date()
    },
    {
        username: "testuser2", 
        email: "testuser2@example.com",
        passwordHash: "$2b$10$testhash456",
        role: "admin",
        createdAt: new Date()
    }
]);

// Insert sample test sessions
db.test_sessions.insertMany([
    {
        userId: 1,
        token: "test-session-token-123",
        expiresAt: new Date(Date.now() + 24*60*60*1000), // 24 hours from now
        createdAt: new Date()
    }
]);

// Create indexes for performance
db.test_media_items.createIndex({ "title": 1 });
db.test_media_items.createIndex({ "filePath": 1 });
db.test_media_items.createIndex({ "mediaType": 1 });
db.test_media_items.createIndex({ "createdAt": 1 });

db.test_users.createIndex({ "username": 1 }, { unique: true });
db.test_users.createIndex({ "email": 1 }, { unique: true });

db.test_sessions.createIndex({ "token": 1 }, { unique: true });
db.test_sessions.createIndex({ "userId": 1 });
db.test_sessions.createIndex({ "expiresAt": 1 });

db.performance_metrics.createIndex({ "testName": 1 });
db.performance_metrics.createIndex({ "timestamp": 1 });

db.test_logs.createIndex({ "timestamp": 1 });
db.test_logs.createIndex({ "level": 1 });
db.test_logs.createIndex({ "service": 1 });

// Print initialization log
print("MongoDB test database initialized successfully");
print("Created collections: test_media_items, test_users, test_sessions, performance_metrics, test_logs");
print("Sample test data inserted");
print("Indexes created for performance");