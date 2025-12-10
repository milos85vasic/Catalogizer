#!/bin/bash

# Simple API Test
# Tests basic API connectivity and endpoints

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORTS_DIR="$PROJECT_ROOT/reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

mkdir -p "$REPORTS_DIR"
LOG_FILE="$REPORTS_DIR/simple-api-test-$TIMESTAMP.log"

echo "Simple API Test - $(date)" > "$LOG_FILE"
echo "========================" >> "$LOG_FILE"

log() {
    echo "$1" | tee -a "$LOG_FILE"
}

print_status() {
    local status=$1
    local message=$2
    case $status in
        "SUCCESS")
            echo -e "${GREEN}✅ $message${NC}"
            ;;
        "ERROR")
            echo -e "${RED}❌ $message${NC}"
            ;;
        "WARNING")
            echo -e "${YELLOW}⚠️  $message${NC}"
            ;;
        "INFO")
            echo -e "${BLUE}ℹ️  $message${NC}"
            ;;
    esac
}

log "Starting API tests..."

# Test if PostgreSQL is running (as prerequisite)
test_prereq() {
    log "Testing prerequisites..."
    
    # Check if PostgreSQL is running
    if docker ps --filter "name=catalogizer-postgres-dev" --format "table {{.Names}}" | grep -q "catalogizer-postgres-dev"; then
        print_status "SUCCESS" "PostgreSQL is running"
    else
        print_status "ERROR" "PostgreSQL is not running"
        return 1
    fi
    
    # Check if Redis is running
    if docker ps --filter "name=catalogizer-redis-dev" --format "table {{.Names}}" | grep -q "catalogizer-redis-dev"; then
        print_status "SUCCESS" "Redis is running"
    else
        print_status "ERROR" "Redis is not running"
        return 1
    fi
    
    return 0
}

# Test API connectivity
test_api_connectivity() {
    log "Testing API connectivity..."
    
    # First try to start the API container with a simple Go command
    if ! docker ps --filter "name=catalogizer-api-dev" --format "table {{.Names}}" | grep -q "catalogizer-api-dev"; then
        log "API container is not running, attempting to start..."
        print_status "WARNING" "API container is not running, starting with simple command..."
        
        # Try to start with a simpler command that doesn't use the problematic library
        docker run -d --name catalogizer-api-test \
            --network catalogizer-network \
            -p 8080:8080 \
            -e DATABASE_HOST=postgres \
            -e DATABASE_PORT=5432 \
            -e DATABASE_USER=catalogizer \
            -e DATABASE_PASSWORD=dev_password_change_me \
            -e DATABASE_NAME=catalogizer_dev \
            -e REDIS_HOST=redis \
            -e REDIS_PORT=6379 \
            -e APP_ENV=development \
            -v "$(pwd)/catalog-api:/app" \
            --workdir /app \
            golang:1.25-alpine \
            go run main.go
            
        # Wait a bit for startup
        sleep 5
        
        # Check if container started
        if docker ps --filter "name=catalogizer-api-test" --format "table {{.Names}}" | grep -q "catalogizer-api-test"; then
            print_status "SUCCESS" "Test API container started"
        else
            print_status "ERROR" "Failed to start test API container"
            return 1
        fi
    fi
    
    # Test basic connectivity
    local attempts=0
    local max_attempts=10
    
    while [ $attempts -lt $max_attempts ]; do
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health | grep -q "200\|404"; then
            print_status "SUCCESS" "API is reachable"
            return 0
        fi
        
        attempts=$((attempts + 1))
        print_status "INFO" "Attempt $attempts/$max_attempts: API not ready yet"
        sleep 2
    done
    
    print_status "ERROR" "API is not reachable after $max_attempts attempts"
    return 1
}

# Test API endpoints
test_api_endpoints() {
    log "Testing API endpoints..."
    
    # Test health endpoint (if it exists)
    local health_response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health 2>/dev/null || echo "000")
    
    if [ "$health_response" = "200" ]; then
        print_status "SUCCESS" "Health endpoint (/health) returned 200"
    elif [ "$health_response" = "404" ]; then
        print_status "WARNING" "Health endpoint not found (404), trying alternatives..."
        
        # Try root endpoint
        local root_response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/ 2>/dev/null || echo "000")
        if [ "$root_response" = "200" ]; then
            print_status "SUCCESS" "Root endpoint (/) returned 200"
        else
            print_status "ERROR" "Neither health nor root endpoint responded"
        fi
    else
        print_status "ERROR" "Health endpoint failed with code: $health_response"
    fi
    
    # Test API documentation or info endpoint
    local info_response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1 2>/dev/null || echo "000")
    if [ "$info_response" = "200" ] || [ "$info_response" = "404" ]; then
        print_status "SUCCESS" "API path reachable (response: $info_response)"
    else
        print_status "WARNING" "API path not reachable (response: $info_response)"
    fi
}

# Test database connectivity from API perspective
test_api_db_connectivity() {
    log "Testing API database connectivity..."
    
    # Test API's ability to connect to database via API endpoint
    local db_test_response=$(curl -s -X POST -H "Content-Type: application/json" \
        -d '{"test": "connection"}' \
        -o /dev/null -w "%{http_code}" \
        http://localhost:8080/api/v1/test/db 2>/dev/null || echo "000")
    
    if [ "$db_test_response" = "200" ]; then
        print_status "SUCCESS" "API database connection test passed"
    elif [ "$db_test_response" = "404" ]; then
        print_status "WARNING" "Database test endpoint not found (404)"
    else
        print_status "WARNING" "Database test endpoint returned: $db_test_response"
    fi
}

# Cleanup function
cleanup() {
    log "Cleaning up test containers..."
    
    if docker ps --filter "name=catalogizer-api-test" --format "table {{.Names}}" | grep -q "catalogizer-api-test"; then
        docker stop catalogizer-api-test >> "$LOG_FILE" 2>&1
        docker rm catalogizer-api-test >> "$LOG_FILE" 2>&1
        print_status "INFO" "Test API container cleaned up"
    fi
}

# Set trap for cleanup on exit
trap cleanup EXIT

# Run tests
log "=== Prerequisite Tests ==="
test_prereq

log ""
log "=== API Connectivity Tests ==="
if test_api_connectivity; then
    log ""
    log "=== API Endpoint Tests ==="
    test_api_endpoints
    
    log ""
    log "=== API Database Tests ==="
    test_api_db_connectivity
else
    print_status "ERROR" "API connectivity tests failed"
    exit 1
fi

log ""
log "Test completed at $(date)"
log "Log file: $LOG_FILE"

echo ""
print_status "INFO" "Full log available at: $LOG_FILE"