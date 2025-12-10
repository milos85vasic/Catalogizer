#!/bin/bash

# Simple Database Connectivity Test
# Basic test for PostgreSQL connectivity

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
LOG_FILE="$REPORTS_DIR/simple-db-test-$TIMESTAMP.log"

echo "Simple Database Connectivity Test - $(date)" > "$LOG_FILE"
echo "========================================" >> "$LOG_FILE"

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

log "Starting database connectivity tests..."

# Test PostgreSQL
test_postgresql() {
    local container_name="catalogizer-postgres-dev"
    local host="postgres"
    local port="5432"
    local user="catalogizer"
    local database="catalogizer_dev"
    
    log "Testing PostgreSQL: $container_name"
    
    # Check if container is running
    if ! docker ps --filter "name=$container_name" --format "table {{.Names}}" | grep -q "$container_name"; then
        print_status "ERROR" "PostgreSQL container not running"
        return 1
    fi
    
    # Test basic connectivity
    if docker exec "$container_name" pg_isready -U "$user" >> "$LOG_FILE" 2>&1; then
        print_status "SUCCESS" "PostgreSQL: Basic connectivity"
        
        # Test CRUD operations
        if docker exec "$container_name" psql -U "$user" -d "$database" -c "SELECT 1;" >> "$LOG_FILE" 2>&1; then
            print_status "SUCCESS" "PostgreSQL: Query execution"
        else
            print_status "ERROR" "PostgreSQL: Query execution"
        fi
    else
        print_status "ERROR" "PostgreSQL: Basic connectivity"
    fi
}

# Test Redis
test_redis() {
    local container_name="catalogizer-redis-dev"
    local host="redis"
    local port="6379"
    
    log "Testing Redis: $container_name"
    
    # Check if container is running
    if ! docker ps --filter "name=$container_name" --format "table {{.Names}}" | grep -q "$container_name"; then
        print_status "ERROR" "Redis container not running"
        return 1
    fi
    
    # Test basic connectivity
    if docker exec "$container_name" redis-cli ping >> "$LOG_FILE" 2>&1; then
        print_status "SUCCESS" "Redis: Basic connectivity"
        
        # Test basic operations
        if docker exec "$container_name" redis-cli set test_key test_value >> "$LOG_FILE" 2>&1 && \
           docker exec "$container_name" redis-cli get test_key >> "$LOG_FILE" 2>&1; then
            print_status "SUCCESS" "Redis: Basic operations"
        else
            print_status "ERROR" "Redis: Basic operations"
        fi
    else
        print_status "ERROR" "Redis: Basic connectivity"
    fi
}

# Run tests
log "=== PostgreSQL Tests ==="
test_postgresql

log ""
log "=== Redis Tests ==="
test_redis

log ""
log "Test completed at $(date)"
log "Log file: $LOG_FILE"

echo ""
print_status "INFO" "Full log available at: $LOG_FILE"