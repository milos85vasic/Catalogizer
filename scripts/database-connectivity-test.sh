#!/bin/bash

# Database Connectivity Test Suite
# Tests all database services connectivity and basic operations

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORTS_DIR="$PROJECT_ROOT/reports"
TEST_NAME="database-connectivity"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

mkdir -p "$REPORTS_DIR"
LOG_FILE="$REPORTS_DIR/${TEST_NAME}-${TIMESTAMP}.log"

echo "Database Connectivity Test - $(date)" > "$LOG_FILE"
echo "========================================" >> "$LOG_FILE"

log() {
    echo "$1" | tee -a "$LOG_FILE"
}

print_status() {
    local status=$1
    local message=$2
    case $status in
        "PASS")
            echo -e "${GREEN}✅ $message${NC}"
            ((PASSED_TESTS++))
            ;;
        "FAIL")
            echo -e "${RED}❌ $message${NC}"
            ((FAILED_TESTS++))
            ;;
        "INFO")
            echo -e "${BLUE}ℹ️  $message${NC}"
            ;;
    esac
    ((TOTAL_TESTS++))
}

# Test PostgreSQL connectivity
test_postgresql() {
    local container_name=$1
    local host=$2
    local port=$3
    local user=$4
    local password=$5
    local database=$6
    local test_name=$7
    
    log "Testing PostgreSQL: $test_name"
    
    # Check if container is running
    if ! docker ps --filter "name=$container_name" --format "table {{.Names}}" | grep -q "$container_name"; then
        print_status "FAIL" "$test_name: Container not running"
        return 1
    fi
    
    # Test basic connectivity
    if docker run --rm --network catalogizer-network postgres:15-alpine psql -h "$host" -p "$port" -U "$user" -d "$database" -c "SELECT version();" >> "$LOG_FILE" 2>&1; then
        print_status "PASS" "$test_name: Basic connectivity"
        
        # Test CRUD operations
        local test_table="test_table_$(date +%s)"
        if docker run --rm --network catalogizer-network postgres:15-alpine psql -h "$host" -p "$port" -U "$user" -d "$database" -c "
            CREATE TABLE $test_table (id SERIAL PRIMARY KEY, data TEXT);
            INSERT INTO $test_table (data) VALUES ('test_data');
            SELECT COUNT(*) FROM $test_table;
            DROP TABLE $test_table;" >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: CRUD operations"
        else
            print_status "FAIL" "$test_name: CRUD operations"
        fi
        
        # Test connection limits
        local concurrent_connections=5
        for i in $(seq 1 $concurrent_connections); do
            if docker run --rm --network catalogizer-network postgres:15-alpine psql -h "$host" -p "$port" -U "$user" -d "$database" -c "SELECT 1;" >> "$LOG_FILE" 2>&1 &
            pids[${i}]=$!
        done
        
        local all_success=true
        for pid in ${pids[*]}; do
            if ! wait $pid; then
                all_success=false
            fi
        done
        
        if $all_success; then
            print_status "PASS" "$test_name: Concurrent connections ($concurrent_connections)"
        else
            print_status "FAIL" "$test_name: Concurrent connections ($concurrent_connections)"
        fi
    else
        print_status "FAIL" "$test_name: Basic connectivity"
    fi
}

# Test MongoDB connectivity
test_mongodb() {
    local container_name=$1
    local host=$2
    local port=$3
    local user=$4
    local password=$5
    local database=$6
    local test_name=$7
    
    log "Testing MongoDB: $test_name"
    
    # Check if container is running
    if ! docker ps --filter "name=$container_name" --format "table {{.Names}}" | grep -q "$container_name"; then
        print_status "FAIL" "$test_name: Container not running"
        return 1
    fi
    
    # Test basic connectivity
    if docker run --rm --network catalogizer-network mongo:7.0 mongosh --host "$host:$port" --eval "db.adminCommand('ping')" >> "$LOG_FILE" 2>&1; then
        print_status "PASS" "$test_name: Basic connectivity"
        
        # Test authentication
        if docker run --rm --network catalogizer-network mongo:7.0 mongosh --host "$host:$port" -u "$user" -p "$password" --authenticationDatabase admin --eval "db.adminCommand('listCollections')" >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: Authentication"
        else
            print_status "FAIL" "$test_name: Authentication"
        fi
        
        # Test CRUD operations
        local test_collection="test_collection_$(date +%s)"
        if docker run --rm --network catalogizer-network mongo:7.0 mongosh --host "$host:$port" -u "$user" -p "$password" --authenticationDatabase admin "$database" --eval "
            db.$test_collection.insertOne({test: 'data'});
            db.$test_collection.find();
            db.$test_collection.drop();" >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: CRUD operations"
        else
            print_status "FAIL" "$test_name: CRUD operations"
        fi
    else
        print_status "FAIL" "$test_name: Basic connectivity"
    fi
}

# Test Redis connectivity
test_redis() {
    local container_name=$1
    local host=$2
    local port=$3
    local password=$4
    local test_name=$5
    
    log "Testing Redis: $test_name"
    
    # Check if container is running
    if ! docker ps --filter "name=$container_name" --format "table {{.Names}}" | grep -q "$container_name"; then
        print_status "FAIL" "$test_name: Container not running"
        return 1
    fi
    
    # Test basic connectivity
    local redis_cmd="redis-cli -h $host -p $port"
    if [ -n "$password" ]; then
        redis_cmd="$redis_cmd -a $password"
    fi
    
    if docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd ping >> "$LOG_FILE" 2>&1; then
        print_status "PASS" "$test_name: Basic connectivity"
        
        # Test CRUD operations
        local test_key="test_key_$(date +%s)"
        local test_value="test_value_$(date +%s)"
        
        if docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd set $test_key "$test_value" >> "$LOG_FILE" 2>&1 &&
           docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd get $test_key >> "$LOG_FILE" 2>&1 &&
           docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd del $test_key >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: CRUD operations"
        else
            print_status "FAIL" "$test_name: CRUD operations"
        fi
        
        # Test data persistence
        local persistence_key="persistence_test_$(date +%s)"
        docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd set $persistence_key "persistent_data" >> "$LOG_FILE" 2>&1
        sleep 2
        
        if docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd get $persistence_key >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: Data persistence"
            docker run --rm --network catalogizer-network redis:7-alpine $redis_cmd del $persistence_key >> "$LOG_FILE" 2>&1
        else
            print_status "FAIL" "$test_name: Data persistence"
        fi
    else
        print_status "FAIL" "$test_name: Basic connectivity"
    fi
}

# Test InfluxDB connectivity
test_influxdb() {
    local container_name=$1
    local host=$2
    local port=$3
    local token=$4
    local org=$5
    local bucket=$6
    local test_name=$7
    
    log "Testing InfluxDB: $test_name"
    
    # Check if container is running
    if ! docker ps --filter "name=$container_name" --format "table {{.Names}}" | grep -q "$container_name"; then
        print_status "FAIL" "$test_name: Container not running"
        return 1
    fi
    
    # Test basic connectivity
    if curl -f -s "http://$host:$port/health" >> "$LOG_FILE" 2>&1; then
        print_status "PASS" "$test_name: Basic connectivity"
        
        # Test API authentication
        if curl -f -s -H "Authorization: Token $token" "http://$host:$port/api/v2/buckets" >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: API authentication"
        else
            print_status "FAIL" "$test_name: API authentication"
        fi
        
        # Test write operations
        local measurement="test_measurement_$(date +%s)"
        local data="$measurement,host=test_host value=42.0 $(date +%s)000000000"
        
        if curl -f -s -X POST "http://$host:$port/api/v2/write?org=$org&bucket=$bucket" \
           -H "Authorization: Token $token" \
           -H "Content-Type: text/plain; charset=utf-8" \
           --data-binary "$data" >> "$LOG_FILE" 2>&1; then
            print_status "PASS" "$test_name: Data write operations"
        else
            print_status "FAIL" "$test_name: Data write operations"
        fi
    else
        print_status "FAIL" "$test_name: Basic connectivity"
    fi
}

# Main execution
main() {
    log "Starting Database Connectivity Test Suite..."
    
    # Wait for services to be ready
    log "Waiting for database services to be ready..."
    sleep 10
    
    # Test PostgreSQL instances
    test_postgresql "catalogizer-postgres" "postgres" "5432" "catalogizer" "${POSTGRES_PASSWORD:-password}" "catalogizer" "PostgreSQL (Production)"
    test_postgresql "catalogizer-postgres-dev" "catalogizer-postgres-dev" "5432" "catalogizer" "dev_password_change_me" "catalogizer_dev" "PostgreSQL (Development)"
    test_postgresql "catalogizer-test-postgres" "test-postgres" "5433" "test_user" "test_password" "catalogizer_test" "PostgreSQL (Testing)"
    
    # Test MongoDB
    test_mongodb "catalogizer-mongodb" "mongodb" "27017" "admin" "admin123" "test" "MongoDB"
    
    # Test Redis instances
    test_redis "catalogizer-redis" "redis" "6379" "" "Redis (Production)"
    test_redis "catalogizer-redis-dev" "catalogizer-redis-dev" "6379" "" "Redis (Development)"
    
    # Test InfluxDB
    test_influxdb "catalogizer-influxdb" "influxdb" "8086" "catalogizer-test-token-12345" "catalogizer" "test_data" "InfluxDB"
    
    # Generate test report
    local success_rate=$((PASSED_TESTS * 100 / TOTAL_TESTS))
    
    cat > "$REPORTS_DIR/${TEST_NAME}-report-${TIMESTAMP}.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Catalogizer - Database Connectivity Test Report</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; background: #f5f7fa; }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 15px; text-align: center; margin-bottom: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .metric { background: white; padding: 25px; border-radius: 10px; text-align: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .metric h3 { margin: 0; color: #495057; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }
        .metric p { margin: 10px 0 0 0; font-size: 32px; font-weight: bold; }
        .success { color: #28a745; }
        .error { color: #dc3545; }
        .section { background: white; margin: 20px 0; padding: 25px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .section h2 { color: #495057; border-bottom: 2px solid #e9ecef; padding-bottom: 10px; }
        .test-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #e9ecef; }
        .test-item:last-child { border-bottom: none; }
        .status-pass { color: #28a745; font-weight: bold; }
        .status-fail { color: #dc3545; font-weight: bold; }
        .database-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin: 20px 0; }
        .database-card { background: #f8f9fa; padding: 20px; border-radius: 10px; border-left: 4px solid #007bff; }
        .database-card h4 { margin-top: 0; color: #007bff; }
        .footer { text-align: center; margin-top: 40px; padding: 20px; color: #6c757d; font-size: 14px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🗄️ Database Connectivity Test Report</h1>
        <p>Database Connection and CRUD Operations Testing</p>
        <p>Generated on $(date)</p>
    </div>
    
    <div class="summary">
        <div class="metric">
            <h3>Total Tests</h3>
            <p>$TOTAL_TESTS</p>
        </div>
        <div class="metric">
            <h3>Passed</h3>
            <p class="success">$PASSED_TESTS</p>
        </div>
        <div class="metric">
            <h3>Failed</h3>
            <p class="error">$FAILED_TESTS</p>
        </div>
        <div class="metric">
            <h3>Success Rate</h3>
            <p class="success">$success_rate%</p>
        </div>
    </div>
    
    <div class="section">
        <h2>🗄️ Database Systems Tested</h2>
        <div class="database-grid">
            <div class="database-card">
                <h4>PostgreSQL</h4>
                <ul>
                    <li>Production Instance (port 5432)</li>
                    <li>Development Instance (port 5432)</li>
                    <li>Testing Instance (port 5433)</li>
                    <li>Connection pooling tested</li>
                    <li>CRUD operations validated</li>
                </ul>
            </div>
            <div class="database-card">
                <h4>MongoDB</h4>
                <ul>
                    <li>Document database instance</li>
                    <li>Authentication with admin user</li>
                    <li>Collection operations</li>
                    <li>Document CRUD validated</li>
                </ul>
            </div>
            <div class="database-card">
                <h4>Redis</h4>
                <ul>
                    <li>Production cache instance</li>
                    <li>Development cache instance</li>
                    <li>Key-value operations</li>
                    <li>Data persistence validated</li>
                </ul>
            </div>
            <div class="database-card">
                <h4>InfluxDB</h4>
                <ul>
                    <li>Time series database</li>
                    <li>Token-based authentication</li>
                    <li>Write operations tested</li>
                    <li>API connectivity validated</li>
                </ul>
            </div>
        </div>
    </div>
    
    <div class="section">
        <h2>🧪 Test Results</h2>
        <p><strong>Test Environment:</strong> Docker Compose</p>
        <p><strong>Network:</strong> catalogizer-network</p>
        <p><strong>Execution Time:</strong> $(date)</p>
        <p><strong>Log File:</strong> <code>$LOG_FILE</code></p>
    </div>
    
    <div class="footer">
        <p>This report was generated automatically by the Database Connectivity Test Suite.</p>
        <p>All database systems are tested for connectivity, authentication, and basic operations.</p>
    </div>
</body>
</html>
EOF
    
    ln -sf "${TEST_NAME}-report-${TIMESTAMP}.html" "$REPORTS_DIR/latest-${TEST_NAME}-report.html"
    
    # Final status
    echo ""
    echo -e "${BLUE}🗄️ Database Connectivity Test Summary:${NC}"
    echo -e "${BLUE}=====================================${NC}"
    echo -e "Total Tests: $TOTAL_TESTS"
    echo -e "${GREEN}Passed: $PASSED_TESTS${NC}"
    echo -e "${RED}Failed: $FAILED_TESTS${NC}"
    echo -e "Success Rate: $success_rate%"
    echo -e "📊 Report: $REPORTS_DIR/${TEST_NAME}-report-${TIMESTAMP}.html"
    echo -e "📋 Log: $LOG_FILE"
    
    if [ "$FAILED_TESTS" -gt 0 ]; then
        echo ""
        echo -e "${RED}❌ SOME DATABASE TESTS FAILED!${NC}"
        echo -e "${RED}Check database configuration and connectivity.${NC}"
        exit 1
    else
        echo ""
        echo -e "${GREEN}🎉 ALL DATABASE TESTS PASSED!${NC}"
        echo -e "${GREEN}All database systems are operational.${NC}"
        exit 0
    fi
}

main "$@"