package config

import (
	"os"
	"path/filepath"
	"testing"
)

func TestLoadConfig(t *testing.T) {
	// Test with non-existent file (should create default)
	tempDir := t.TempDir()
	configPath := filepath.Join(tempDir, "config.json")
	
	config, err := LoadConfig(configPath)
	if err != nil {
		t.Fatalf("Failed to load config: %v", err)
	}
	
	// Should return default config
	if config.Server.Port != 8080 {
		t.Errorf("Expected default port 8080, got %d", config.Server.Port)
	}
}

func TestLoadConfigWithExistingFile(t *testing.T) {
	tempDir := t.TempDir()
	configPath := filepath.Join(tempDir, "config.json")
	
	// Create a test config file with valid JWT secret
	testConfig := `{
		"server": {
			"port": 9000,
			"host": "localhost"
		},
		"database": {
			"path": "test.db"
		},
		"auth": {
			"jwt_secret": "valid-secret-for-testing-that-is-long-enough",
			"admin_username": "admin",
			"admin_password": "password"
		}
	}`
	
	err := os.WriteFile(configPath, []byte(testConfig), 0644)
	if err != nil {
		t.Fatalf("Failed to write test config: %v", err)
	}
	
	config, err := LoadConfig(configPath)
	if err != nil {
		t.Fatalf("Failed to load config: %v", err)
	}
	
	// Should load from file
	if config.Server.Port != 9000 {
		t.Errorf("Expected port 9000, got %d", config.Server.Port)
	}
	if config.Server.Host != "localhost" {
		t.Errorf("Expected host 'localhost', got %s", config.Server.Host)
	}
}

func TestValidateConfig(t *testing.T) {
	tests := []struct {
		name        string
		config      *Config
		expectError bool
	}{
		{
			name: "valid config",
			config: &Config{
				Server: ServerConfig{Port: 8080},
				Database: DatabaseConfig{Path: "test.db"},
				Auth: AuthConfig{JWTSecret: "valid-secret"},
				Catalog: CatalogConfig{DefaultPageSize: 50, MaxPageSize: 100},
			},
			expectError: false,
		},
		{
			name: "invalid port",
			config: &Config{
				Server: ServerConfig{Port: -1},
			},
			expectError: true,
		},
		{
			name: "empty database path",
			config: &Config{
				Server:   ServerConfig{Port: 8080},
				Database: DatabaseConfig{Path: ""},
			},
			expectError: true,
		},
		{
			name: "default JWT secret",
			config: &Config{
				Server:   ServerConfig{Port: 8080},
				Database: DatabaseConfig{Path: "test.db"},
				Auth:     AuthConfig{EnableAuth: true, JWTSecret: "change-this-secret-in-production"},
			},
			expectError: true,
		},
	}
	
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := validateConfig(tt.config)
			if tt.expectError && err == nil {
				t.Errorf("Expected error but got none")
			}
			if !tt.expectError && err != nil {
				t.Errorf("Unexpected error: %v", err)
			}
		})
	}
}

func TestGetDefaultConfig(t *testing.T) {
	config := getDefaultConfig()
	
	if config.Server.Port != 8080 {
		t.Errorf("Expected default port 8080, got %d", config.Server.Port)
	}
	
	if config.Server.Host != "localhost" {
		t.Errorf("Expected default host 'localhost', got %s", config.Server.Host)
	}
	
	if config.Database.Path != "./catalog.db" {
		t.Errorf("Expected default database path './catalog.db', got %s", config.Database.Path)
	}
}